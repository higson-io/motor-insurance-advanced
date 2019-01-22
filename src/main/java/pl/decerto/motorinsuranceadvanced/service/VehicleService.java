package pl.decerto.motorinsuranceadvanced.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.decerto.app.model.Vehicle;

@Service
public class VehicleService {

	private final QuoteService quoteService;

	@Autowired
	public VehicleService(QuoteService quoteService) {
		this.quoteService = quoteService;
	}

	public Vehicle getVehicle() {
		return quoteService.getQuote().getVehicle();
	}
}
