package pl.decerto.motorinsuranceadvanced.example;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import pl.decerto.app.model.Address;
import pl.decerto.app.model.BundleRoot;
import pl.decerto.app.model.Coverage;
import pl.decerto.app.model.Discount;
import pl.decerto.app.model.Driver;
import pl.decerto.app.model.Option;
import pl.decerto.app.model.Quote;
import pl.decerto.app.model.Vehicle;
import pl.decerto.app.service.HyperonPersistenceService;
import pl.decerto.hyperon.persistence.context.AppCtx;
import pl.decerto.hyperon.persistence.proxy.HyperonPersistence;
import pl.decerto.hyperon.persistence.service.BundleService;
import pl.decerto.hyperon.runtime.core.HyperonContext;
import pl.decerto.hyperon.runtime.core.HyperonEngine;
import pl.decerto.hyperon.runtime.model.HyperonDomainObject;
import pl.decerto.hyperon.runtime.rhino.RhinoDate;
import pl.decerto.motorinsuranceadvanced.dao.QuoteRepository;

@PropertySource("classpath:application.yml")
@Component
@DependsOn("DBGmoInitializer")
public class Motor {

	private static final Logger log = LoggerFactory.getLogger(Motor.class);

	private final HyperonEngine engine;

	private final QuoteValidator quoteValidator;

	private final HyperonPersistenceService service;

	private final BundleService persistence;

	private final QuoteRepository quoteRepository;

	@Autowired
	public Motor(HyperonEngine engine, QuoteValidator quoteValidator, HyperonPersistenceService service,
		BundleService persistence, QuoteRepository quoteRepository, @Value("${hyperon.profile}") String profile) {
		this.engine = engine;
		this.quoteValidator = quoteValidator;
		this.service = service;
		this.persistence = persistence;
		this.quoteRepository = quoteRepository;

		HyperonPersistence.setDefaultBundleDef(this.persistence.getDefinition(profile));
	}

	/*
		====================================================
			 get available coverages for plan FULL
		====================================================
	 */
	public BundleRoot createBundleRoot() {
		return quoteRepository.findLastQuoteId().map(q -> this.service.load(q.longValue()))
			.orElseGet(this::createQuoteForFirstTime);
	}

	private BundleRoot createQuoteForFirstTime() {
		log.info("begin ======== CREATE QUOTE ========");

		// basic configuration:
		//  1. plan
		//  2. options defined for this plan
		//  3. coverages defined for this plan

		// 1. obtain FULL plan handle
		HyperonDomainObject plan = engine.getDomain("DEMO", "/PLANS[FULL]");

		// 2. all rating options
		List<HyperonDomainObject> options = plan.getChildren("OPTIONS");

		// 3. all coverages for this plan
		List<HyperonDomainObject> coverages = plan.getChildren("COVERAGES");

		// 4. create bundleRoot
		BundleRoot bundleRoot = buildQuote(plan, options, coverages);
		log.info("end   ======== CREATE QUOTE ======== \n");

		return bundleRoot;
	}

	public BundleRoot recalculate(BundleRoot bundleRoot) {
		var quote = bundleRoot.getQuote();
		quoteValidator.validateQuote(quote);

		// handle to plan
		HyperonDomainObject plan = engine.getDomain("DEMO", "/").getChild("PLANS", quote.getPlanCode());

		var driver = quote.getDriver();
		driver.setAge((long) computeAge(driver));

		// for each option and each coverage
		for (var option : quote.getOptions()) {
			rebuildCoverages(plan.getChildren("COVERAGES"), option);
			// calculate premium for each coverage
			for (var coverage : option.getCoverages()) {

				// coverage definition handle
				var coverDef = plan.getChild("COVERAGES", coverage.getCode());

				// dynamic context - derives all paths from current coverage
				var ctx = new AppCtx(coverage.unproxy());

				var limit1 = coverDef.getAttrDecimal("LIMIT_1", ctx);
				var limit2 = coverDef.getAttrDecimal("LIMIT_2", ctx);

				coverage.setLimit1(limit1);
				coverage.setLimit2(limit2);

				// get PREMIUM attribute
				var premium = coverDef.getAttr("PREMIUM").getDecimal(ctx);
				coverage.setPremium(premium);
			}

			var ctx = new AppCtx(option.unproxy());

			// check discounts
			var possibleDiscounts = getPossibleDiscounts(plan, ctx);
			option.getDiscounts().clear();
			option.getDiscounts().addAll(possibleDiscounts);
		}
		if (log.isInfoEnabled()) {
			log.info(bundleRoot.getBundle().print());
		}
		this.service.persist(bundleRoot);
		return bundleRoot;
	}

	private int computeAge(Driver driver) {
		return RhinoDate.getAbsoluteYearDiff(driver.getBirthDate(), new Date());
	}

	private List<Discount> getPossibleDiscounts(HyperonDomainObject plan, AppCtx ctx) {
		return plan.getChildren("DISCOUNTS")
			.stream()
			.filter(d -> d.getAttrBoolean("available", ctx))
			.map(d -> buildDiscount(d, ctx))
			.sorted(Comparator.comparingInt(d -> Math.toIntExact(d.getPosition())))
			.collect(Collectors.toList());
	}

	private Discount buildDiscount(HyperonDomainObject domainObjectDiscount, AppCtx ctx) {
		var value = domainObjectDiscount.getAttr("value").getDecimal(ctx);
		var position = domainObjectDiscount.getAttr("position").longValue(ctx);

		var discount = new Discount();
		discount.setCode(domainObjectDiscount.getCode());
		discount.setName(domainObjectDiscount.getName());
		discount.setValue(value);
		discount.setPosition(position);
		return discount;
	}

	/**
	 * @param plan      rating plan
	 * @param options   all options for this plan
	 * @param coverages all coverages fot this plan
	 * @return constructed quote with coverages for all options
	 */
	private BundleRoot buildQuote(HyperonDomainObject plan, List<HyperonDomainObject> options, List<HyperonDomainObject> coverages) {

		// sample driver's data
		var address = new Address();
		address.setCity("Lake Jackson");
		address.setStreet("Allwood St");
		address.setZipCode("77566");

		var dateOfBirth = Date.from(LocalDate.now().minusYears(40).atStartOfDay(ZoneId.systemDefault()).toInstant());
		var driver = new Driver()
			.setFirstname("John")
			.setLastname("Potter")
			.setGender("M")
			.setBirthDate(dateOfBirth)
			.setLicenceObtainedAtAge(18L)
			.setNumberOfAccidents(0L)
			.setNumberOfTickets(0L)
			.setAddress(address);

		var vehicle = new Vehicle();
		vehicle.setProductionYear(2010L);
		vehicle.setMakeId(217L);
		vehicle.setTypeId(28654L);
		vehicle.setModelId(218915L);

		var quote = new Quote();
		quote.setPlanCode(plan.getCode());
		quote.setDriver(driver);
		quote.setVehicle(vehicle);

		for (var o : options) {
			var option = new Option();
			option.setCode(o.getCode());
			option.setOrder(o.getAttribute("ORDER").getLong(new HyperonContext()));
			quote.getOptions().add(option);

			rebuildCoverages(coverages, option);
		}
		var root = service.create();
		root.setQuote(quote);
		return root;
	}

	private void rebuildCoverages(List<HyperonDomainObject> coverages, Option option) {
		for (var c : coverages) {
			// change that to AppCtx with coverage only
			HyperonContext ctx = new HyperonContext(
				"option.code", option.getCode(),
				"coverage.code", c.getCode()
			);

			// get IS_AVAILABLE attribute's value
			boolean isAvailable = c.getAttrBoolean("IS_AVAILABLE", ctx);

			Optional<Coverage> optionCoverage = option.getCoverages()
				.stream()
				.filter(cov -> cov.getCode().equals(c.getCode()))
				.findFirst();

			// add/update coverage only if available for this option
			if (isAvailable) {
				if (optionCoverage.isPresent()) {
					// update
					setCoverData(c, optionCoverage.get(), ctx);
				} else {
					// add new
					Coverage cover = new Coverage();
					cover.setCode(c.getCode());
					setCoverData(c, cover, ctx);
					option.getCoverages().add(cover);
				}
			}

			// remove not available existing coverage
			if (!isAvailable && optionCoverage.isPresent()) {
				option.getCoverages().remove(optionCoverage.get());
			}
		}
	}

	private void setCoverData(HyperonDomainObject domainDataCover, Coverage cover, HyperonContext ctx) {
		cover.setName(domainDataCover.getName());
		cover.setPosition(domainDataCover.getAttrInteger("POSITION", ctx).longValue());
		var description = domainDataCover.getAttr("DESCRIPTION");
		cover.setDescription(description != null ? description.getString(ctx) : null);
	}
}
