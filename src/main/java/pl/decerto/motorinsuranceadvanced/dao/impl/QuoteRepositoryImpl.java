package pl.decerto.motorinsuranceadvanced.dao.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import pl.decerto.motorinsuranceadvanced.dao.QuoteRepository;
import pl.decerto.hyperon.persistence.factory.HyperonPersistenceFactory;

@Repository
public class QuoteRepositoryImpl implements QuoteRepository {

	private static final String MAX_QUOTE_ID = "select gmo.id\n" +
		"from %s gmo\n" +
		"inner join (Select max(created) as max_date from %s) max\n" +
		"where gmo.created = max.max_date";

	private final HyperonPersistenceFactory factory;
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public QuoteRepositoryImpl(JdbcTemplate jdbcTemplate, HyperonPersistenceFactory factory) {
		this.jdbcTemplate = jdbcTemplate;
		this.factory = factory;
	}

	@Override public Optional<Integer> findLastQuoteId() {
		var gmoTable = factory.getConf().bundleTable();
		var query = String.format(MAX_QUOTE_ID, gmoTable, gmoTable);

		return jdbcTemplate.query(query, (rs, rowNum) -> rs.getInt("id"))
			.stream()
			.findFirst();
	}
}
