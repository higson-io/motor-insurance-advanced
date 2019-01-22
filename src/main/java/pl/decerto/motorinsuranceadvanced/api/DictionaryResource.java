package pl.decerto.motorinsuranceadvanced.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pl.decerto.motorinsuranceadvanced.api.dto.DictionaryEntryDto;
import pl.decerto.motorinsuranceadvanced.converter.DictionaryEntryConverter;
import pl.decerto.motorinsuranceadvanced.model.DictionaryEntry;
import pl.decerto.motorinsuranceadvanced.service.DictionaryService;

@RestController
@RequestMapping("/dictionaries")
public class DictionaryResource {

	private final DictionaryService dictionaryService;
	private final DictionaryEntryConverter dictionaryEntryConverter;

	@Autowired
	public DictionaryResource(DictionaryService dictionaryService,
		DictionaryEntryConverter dictionaryEntryConverter) {
		this.dictionaryService = dictionaryService;
		this.dictionaryEntryConverter = dictionaryEntryConverter;
	}

	@GetMapping(path = "/make")
	public List<DictionaryEntryDto> getMakeDictionary(@RequestParam("productionYear") Integer productionYear) {

		List<DictionaryEntry> makeDictionary = dictionaryService.getMakeDictionary(productionYear);
		return dictionaryEntryConverter.convertList(makeDictionary);
	}

	@GetMapping(path = "/model")
	public List<DictionaryEntryDto> getModelDictionary(@RequestParam("typeId") Integer typeId) {
		return dictionaryEntryConverter.convertList(dictionaryService.getModelDictionary(typeId));
	}
	@GetMapping(path = "/productionYear")
	public List<DictionaryEntryDto> getProductionYearDictionary() {
		return dictionaryEntryConverter.convertList(dictionaryService.getProductionYearDictionary());
	}

	@GetMapping(path = "/type")
	public List<DictionaryEntryDto> getTypeDictionary(@RequestParam("makeId") Integer makeId) {
		return dictionaryEntryConverter.convertList(dictionaryService.getTypeDictionary(makeId));
	}

}
