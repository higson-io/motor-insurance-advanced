package pl.decerto.motorinsuranceadvanced.dao.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import pl.decerto.hyperon.persistence.factory.HyperonPersistenceFactory;
import pl.decerto.motorinsuranceadvanced.dao.QuoteRepository;

@Repository
public class QuoteRepositoryImpl implements QuoteRepository {

	private static final String MAX_QUOTE_ID = "select per.id\n" +
		"from %s per\n" +
		"inner join (Select max(created) as max_date from %s) max\n" +
		"where per.created = max.max_date";

	private final HyperonPersistenceFactory factory;
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public QuoteRepositoryImpl(JdbcTemplate jdbcTemplate, HyperonPersistenceFactory factory) {
		this.jdbcTemplate = jdbcTemplate;
		this.factory = factory;
	}

	@Override public Optional<Integer> findLastQuoteId() {
		var bundleTable = factory.getConf().bundleTable();
		var query = String.format(MAX_QUOTE_ID, bundleTable, bundleTable);

		return jdbcTemplate.query(query, (rs, rowNum) -> rs.getInt("id"))
			.stream()
			.findFirst();
	}
}
