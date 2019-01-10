package pl.decerto.motorinsuranceadvanced.converter;

import java.util.Objects;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import pl.decerto.app.model.Vehicle;
import pl.decerto.motorinsuranceadvanced.api.dto.VehicleDto;

@Component
public class VehicleConverter implements Converter<Vehicle, VehicleDto> {

	@Override
	public VehicleDto convert(Vehicle vehicle) {
		Objects.requireNonNull(vehicle, "Vehicle can not be null.");
		var vehicleDto = new VehicleDto();
		vehicleDto.setMakeId(vehicle.getMakeId().intValue());
		vehicleDto.setModelId(vehicle.getModelId().intValue());
		vehicleDto.setTypeId(vehicle.getTypeId().intValue());
		vehicleDto.setProductionYear(vehicle.getProductionYear().intValue());
		return vehicleDto;
	}
}
