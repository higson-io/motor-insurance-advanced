package pl.decerto.motorinsuranceadvanced.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.decerto.motorinsuranceadvanced.api.dto.QuoteDto;
import pl.decerto.motorinsuranceadvanced.converter.QuoteConverter;
import pl.decerto.motorinsuranceadvanced.example.Motor;
import pl.decerto.motorinsuranceadvanced.service.QuoteService;

@RestController
@RequestMapping("/quote")
public class QuoteResource {

	private final QuoteService quoteService;

	private final QuoteConverter quoteConverter;

	private final Motor motor;

	@Autowired
	public QuoteResource(QuoteService quoteService, QuoteConverter quoteConverter, Motor motor) {
		this.quoteService = quoteService;
		this.quoteConverter = quoteConverter;
		this.motor = motor;
	}

	@GetMapping
	public QuoteDto getQuote() {
		var quote = quoteService.recalculateQuote();
		return quoteConverter.convert(quote);
	}
}
