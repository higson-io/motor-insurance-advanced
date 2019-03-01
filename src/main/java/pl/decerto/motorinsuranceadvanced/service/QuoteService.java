package pl.decerto.motorinsuranceadvanced.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.decerto.app.model.BundleRoot;
import pl.decerto.app.model.Quote;
import pl.decerto.motorinsuranceadvanced.example.Motor;

@Service
public class QuoteService {

	private final Logger log = LoggerFactory.getLogger(QuoteService.class);

	private final Motor motor;

	private BundleRoot sessionRootContext;

	@Autowired
	public QuoteService(Motor motor) {
		this.motor = motor;
	}

	@PostConstruct
	public void initializeQuote() {
		log.info("PostConstruct for QuoteService");
		initBundleRoot();
	}

	public Quote getQuote() {
		return sessionRootContext.getQuote();
	}

	public Quote recalculateQuote() {
		BundleRoot recalculated = motor.recalculate(sessionRootContext);
		return recalculated.getQuote();
	}

	private void initBundleRoot() {
		if (sessionRootContext == null) {
			sessionRootContext = motor.createBundleRoot();
		}
	}
}
