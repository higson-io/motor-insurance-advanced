package pl.decerto.motorinsuranceadvanced.service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.smartparam.engine.core.context.ParamContext;
import org.smartparam.engine.core.output.MultiValue;
import org.smartparam.engine.core.output.ParamValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pl.decerto.motorinsuranceadvanced.model.DictionaryEntry;
import pl.decerto.hyperon.runtime.core.HyperonContext;
import pl.decerto.hyperon.runtime.core.HyperonEngine;

@Service
public class DictionaryService {

	private static final String AVAILABLE_MODELS = "demo.motor.dict.vehicle.availableModels";
	private final HyperonEngine engine;

	@Autowired
	public DictionaryService(HyperonEngine engine) {
		this.engine = engine;
	}

	public List<DictionaryEntry> getMakeDictionary(int productionYear) {
		return getHyperonDictionary("demo.motor.dict.vehicle.availableMakes", new HyperonContext("quote.vehicle.productionYear", productionYear),
			this::vehicleMakeRowToDictionaryEntry);
	}

	public List<DictionaryEntry> getModelDictionary(Integer typeId) {
		return getHyperonDictionary(AVAILABLE_MODELS, new HyperonContext("quote.vehicle.typeId", typeId),
			this::vehicleModelRowToDictionaryEntry);
	}

	public List<DictionaryEntry> getProductionYearDictionary() {
		return getHyperonDictionary("demo.motor.dict.productionYear", new HyperonContext(), this::rowToSimpleEntry);
	}

	public List<DictionaryEntry> getTypeDictionary(Integer makeId) {
		return getHyperonDictionary("demo.motor.dict.vehicle.availableTypes", new HyperonContext("quote.vehicle.makeId", makeId),
			this::vehicleTypeRowToDictionaryEntry);
	}

	private <T> List<T> getHyperonDictionary(String parameter, ParamContext context, Function<MultiValue, T> mapFunction) {
		ParamValue paramValue = engine.get(parameter, context);
		return getResultFromParamValue(paramValue, mapFunction);
	}

	private <T> List<T> getResultFromParamValue(ParamValue paramValue, Function<MultiValue, T> mapFunction) {
		return paramValue.rows().stream()
			.map(mapFunction)
			.collect(Collectors.toList());
	}

	private DictionaryEntry vehicleMakeRowToDictionaryEntry(MultiValue r) {
		return new DictionaryEntry(r.getString("make_id"), r.getString("make"));
	}

	private DictionaryEntry vehicleModelRowToDictionaryEntry(MultiValue r) {
		return new DictionaryEntry(r.getString("model_id"), r.getString("model_label"));
	}

	private DictionaryEntry vehicleTypeRowToDictionaryEntry(MultiValue r) {
		return new DictionaryEntry(r.getString("type_id"), r.getString("type"));
	}

	private DictionaryEntry rowToSimpleEntry(MultiValue r) {
		return new DictionaryEntry(r.getString("code"), r.getString("name"));
	}

}
