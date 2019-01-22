package pl.decerto.motorinsuranceadvanced.converter;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

import java.util.Objects;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import pl.decerto.app.model.Address;
import pl.decerto.app.model.Driver;
import pl.decerto.motorinsuranceadvanced.api.dto.AddressDto;
import pl.decerto.motorinsuranceadvanced.api.dto.DriverDto;

@Component
public class DriverConverter implements Converter<Driver, DriverDto> {

	@Override
	public DriverDto convert(Driver driver) {
		Objects.requireNonNull(driver, "Driver can not be null.");
		var driverDto = new DriverDto();
		driverDto.setFirstName(driver.getFirstname());
		driverDto.setLastName(driver.getLastname());
		driverDto.setDateOfBirth(driver.getBirthDate());
		driverDto.setGender(driver.getGender());
		driverDto.setNumberOfTickets(firstNonNull(driver.getNumberOfTickets(), 0).intValue());
		driverDto.setNumberOfAccidents(firstNonNull(driver.getNumberOfAccidents(), 0).intValue());
		driverDto.setLicenceObtainedAtAge(firstNonNull(driver.getLicenceObtainedAtAge(), 0).intValue());
		driverDto.setAddress(convertAddress(driver.getAddress()));
		return driverDto;
	}

	private AddressDto convertAddress(Address address) {
		Objects.requireNonNull(address, "Address can not be null.");
		var addressDto = new AddressDto();
		addressDto.setZipCode(address.getZipCode());
		addressDto.setCity(address.getCity());
		addressDto.setStreet(address.getStreet());
		return addressDto;
	}
}
