package pl.decerto.motorinsuranceadvanced.example;

import org.springframework.stereotype.Component;

import pl.decerto.app.model.Driver;
import pl.decerto.app.model.Quote;
import pl.decerto.app.model.Vehicle;
import pl.decerto.motorinsuranceadvanced.exceptions.InvalidCarDataException;
import pl.decerto.motorinsuranceadvanced.exceptions.InvalidDriverDataException;

@Component
public class QuoteValidator {

	public void validateQuote(Quote quote) {
		validateDriver(quote.getDriver());
		validateVehicle(quote.getVehicle());
	}

	private void validateDriver(Driver driver) {
		checkConditionAndThrowInvalidDriverDataException(driver.getBirthDate() == null, "Driver birth date not supplied.");
		checkConditionAndThrowInvalidDriverDataException(driver.getFirstname() == null, "Driver first name not supplied.");
		checkConditionAndThrowInvalidDriverDataException(driver.getGender() == null, "Driver gender not supplied.");
		checkConditionAndThrowInvalidDriverDataException(driver.getAddress() == null, "Driver address not supplied.");
		checkConditionAndThrowInvalidDriverDataException(driver.getAddress().getZipCode() == null, "Driver address zip code not supplied.");
		checkConditionAndThrowInvalidDriverDataException(driver.getAddress().getCity() == null, "Driver address city not supplied.");
		checkConditionAndThrowInvalidDriverDataException(driver.getAddress().getStreet() == null, "Driver address street not supplied.");
	}

	private void checkConditionAndThrowInvalidDriverDataException(boolean condition, String message) {
		if (condition) {
			throw new InvalidDriverDataException(message);
		}
	}

	private void validateVehicle(Vehicle vehicle) {
		checkConditionAndThrowInvalidCarDataException(vehicle.getProductionYear() == 0, "Vehicle production year not supplied.");
		checkConditionAndThrowInvalidCarDataException(vehicle.getMakeId() == 0, "Vehicle make not supplied.");
		checkConditionAndThrowInvalidCarDataException(vehicle.getTypeId() == 0, "Vehicle type not supplied.");
		checkConditionAndThrowInvalidCarDataException(vehicle.getModelId() == 0, "Vehicle model not supplied.");
	}

	private void checkConditionAndThrowInvalidCarDataException(boolean condition, String message) {
		if (condition) {
			throw new InvalidCarDataException(message);
		}
	}
}
