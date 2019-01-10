package pl.decerto.motorinsuranceadvanced.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.decerto.motorinsuranceadvanced.api.dto.VehicleDto;
import pl.decerto.motorinsuranceadvanced.converter.VehicleConverter;
import pl.decerto.motorinsuranceadvanced.service.VehicleService;

@RestController
@RequestMapping("/vehicle")
public class VehicleResource {

	private final VehicleService vehicleService;
	private final VehicleConverter vehicleConverter;

	@Autowired
	public VehicleResource(VehicleService vehicleService, VehicleConverter vehicleConverter) {
		this.vehicleService = vehicleService;
		this.vehicleConverter = vehicleConverter;
	}

	@GetMapping
	public VehicleDto getVehicle() {
		return vehicleConverter.convert(vehicleService.getVehicle());
	}

	@PutMapping("/productionYear")
	public void setProductionYear(@RequestBody Long productionYear) {
		var vehicle = vehicleService.getVehicle();
		vehicle.setProductionYear(productionYear);
		vehicle.setMakeId(0L);
		vehicle.setTypeId(0L);
		vehicle.setModelId(0L);
	}

	@PutMapping("/make")
	public void setMake(@RequestBody Long makeId) {
		var vehicle = vehicleService.getVehicle();
		vehicle.setMakeId(makeId);
		vehicle.setTypeId(0L);
		vehicle.setModelId(0L);
	}

	@PutMapping("/model")
	public void setModel(@RequestBody Long modelId) {
		var vehicle = vehicleService.getVehicle();
		vehicle.setModelId(modelId);
	}

	@PutMapping("/type")
	public void setType(@RequestBody Long typeId) {
		var vehicle = vehicleService.getVehicle();
		vehicle.setTypeId(typeId);
		vehicle.setModelId(0L);
	}
}
