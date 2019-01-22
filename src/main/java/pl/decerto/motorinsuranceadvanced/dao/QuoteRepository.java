package pl.decerto.motorinsuranceadvanced.dao;

import java.util.Optional;

@FunctionalInterface
public interface QuoteRepository {

	Optional<Integer> findLastQuoteId();

}
