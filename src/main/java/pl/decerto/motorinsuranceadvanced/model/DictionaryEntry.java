package pl.decerto.motorinsuranceadvanced.model;

import java.util.Objects;

public class DictionaryEntry {

	private final String code;
	private final String name;

	public DictionaryEntry(String code, String name) {
		this.code = code;
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	@Override public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof DictionaryEntry)) {
			return false;
		}
		DictionaryEntry that = (DictionaryEntry) o;
		return Objects.equals(code, that.code) &&
			Objects.equals(name, that.name);
	}

	@Override public int hashCode() {
		return Objects.hash(code, name);
	}
}
