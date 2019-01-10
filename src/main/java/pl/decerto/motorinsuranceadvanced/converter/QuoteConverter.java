package pl.decerto.motorinsuranceadvanced.converter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import pl.decerto.app.model.Coverage;
import pl.decerto.app.model.Discount;
import pl.decerto.app.model.Option;
import pl.decerto.app.model.Quote;
import pl.decerto.motorinsuranceadvanced.api.dto.CoverageDto;
import pl.decerto.motorinsuranceadvanced.api.dto.DiscountDto;
import pl.decerto.motorinsuranceadvanced.api.dto.OptionDto;
import pl.decerto.motorinsuranceadvanced.api.dto.QuoteDto;

@Component
public class QuoteConverter implements Converter<Quote, QuoteDto> {

	@Override
	public QuoteDto convert(Quote quote) {
		Objects.requireNonNull(quote, "Quote can not be null");
		var quoteDto = new QuoteDto();
		quoteDto.setOptions(quote.getOptions()
			.stream()
			.map(this::toOptionDto)
			.collect(Collectors.toList()));
		return quoteDto;
	}

	private OptionDto toOptionDto(Option option) {
		var optionDto = new OptionDto();
		optionDto.setCode(option.getCode());
		optionDto.setCoverages(convertCoverages(option.getCoverages()));
		optionDto.setDiscounts(convertDiscounts(option.getDiscounts()));
		optionDto.setOrder(option.getOrder().intValue());

		var optionPremium = getOptionPremium(option);
		optionDto.setPremium(optionPremium);
		optionDto.setPremiumBeforeDiscounts(optionPremium);
		updateOptionWithDiscounts(optionDto);
		return optionDto;
	}

	private List<CoverageDto> convertCoverages(List<Coverage> coverages) {
		return coverages
			.stream()
			.map(this::toCoverageDto)
			.collect(Collectors.toList());
	}

	private List<DiscountDto> convertDiscounts(List<Discount> discounts) {
		return discounts
			.stream()
			.map(this::toDiscountDto)
			.collect(Collectors.toList());
	}

	private CoverageDto toCoverageDto(Coverage coverage) {
		var coverageDto = new CoverageDto();
		coverageDto.setCode(coverage.getCode());
		coverageDto.setName(coverage.getName());
		coverageDto.setDescription(coverage.getDescription());
		coverageDto.setLimitLeft(coverage.getLimit1());
		coverageDto.setLimitRight(coverage.getLimit2());
		coverageDto.setPremium(coverage.getPremium());
		coverageDto.setPosition(coverage.getPosition().intValue());
		return coverageDto;
	}

	private DiscountDto toDiscountDto(Discount discount) {
		var discountDto = new DiscountDto();
		discountDto.setName(discount.getName());
		discountDto.setCode(discount.getCode());
		discountDto.setValue(discount.getValue());
		discountDto.setPosition(discount.getPosition().intValue());
		return discountDto;
	}

	private BigDecimal getOptionPremium(Option option) {
		return option.getCoverages()
			.stream()
			.map(Coverage::getPremium)
			.reduce(BigDecimal::add)
			.orElse(BigDecimal.ZERO);
	}

	private void updateOptionWithDiscounts(OptionDto option) {
		List<DiscountDto> discounts = option.getDiscounts();
		for (DiscountDto discount : discounts) {
			BigDecimal oldOptionPremium = option.getPremium();
			option.setPremium(oldOptionPremium.subtract(discount.getValue()));
		}
	}
}
