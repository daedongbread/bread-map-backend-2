package com.depromeet.breadmapbackend.utils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.depromeet.breadmapbackend.domain.search.SearchService;
import com.depromeet.breadmapbackend.mock.FakeSearchServiceImpl;
import com.querydsl.jpa.impl.JPAQueryFactory;

@TestConfiguration
public class TestConfig {

	@PersistenceContext
	private EntityManager entityManager;

	@Bean
	public JPAQueryFactory jpaQueryFactory() {
		return new JPAQueryFactory(entityManager);
	}

	@Primary
	@Bean
	public SearchService searchService() {
		return new FakeSearchServiceImpl();
	}
}
