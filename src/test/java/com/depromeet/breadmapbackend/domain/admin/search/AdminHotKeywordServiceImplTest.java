package com.depromeet.breadmapbackend.domain.admin.search;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

/**
 * AdminHotKeywordServiceImplTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/10/23
 */
class AdminHotKeywordServiceImplTest extends AdminHotKeywordServiceTest {

	@Autowired
	private AdminHotKeywordServiceImpl sut;

	@Test
	@Sql("classpath:hot-keyword-test-data.sql")
	void 인기검색어_수정_테스트() throws Exception {
		//given
		final List<HotKeyword> request = List.of(HotKeyword.createSearchKeyword("test", 2),
			HotKeyword.createSearchKeyword("빵빠라라빵", 1));
		//when
		sut.updateHotKeywordsRank(request);

		//then
		final List<HotKeyword> result = em.createQuery(
			"select h "
				+ "from HotKeyword h "
				+ "order by h.rank asc ", HotKeyword.class
		).getResultList();

		Assertions.assertThat(result.size()).isEqualTo(2);
		Assertions.assertThat(result.get(0).getKeyword()).isEqualTo("빵빠라라빵");
		Assertions.assertThat(result.get(1).getKeyword()).isEqualTo("test");

	}

}