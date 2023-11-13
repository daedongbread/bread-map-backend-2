package com.depromeet.breadmapbackend.domain.admin.search;

import java.util.List;

import lombok.Getter;

/**
 * Keyword
 *
 * @author jaypark
 * @version 1.0.0
 * @since 11/10/23
 */
@Getter
public class Keyword {
	private final Long id;
	private final String keyword;
	private final long oneWeekCount;
	private final long oneMonthCount;
	private final long threeMonthCount;

	public Keyword(
		final Long id,
		final String keyword,
		final long oneWeekCount,
		final long oneMonthCount,
		final long threeMonthCount
	) {
		this.id = id;
		this.keyword = keyword;
		this.oneWeekCount = oneWeekCount;
		this.oneMonthCount = oneMonthCount;
		this.threeMonthCount = threeMonthCount;
	}

	public static List<Keyword> getMockData() {
		return List.of(
			new Keyword(1L, "소금빵", 1, 265, 73),
			new Keyword(2L, "강남역", 2, 212, 653),
			new Keyword(3L, "테스트 검색어", 3, 234, 543),
			new Keyword(4L, "하하하하", 4, 432, 453),
			new Keyword(5L, "호호호", 5, 122, 453),
			new Keyword(6L, "이힝", 6, 122, 5673),
			new Keyword(7L, "가나다라", 7, 322, 653),
			new Keyword(8L, "마바사", 8, 212, 453),
			new Keyword(9L, "아자차카", 9, 212, 6573),
			new Keyword(10L, "타파하", 1111, 223, 2343),
			new Keyword(11L, "abcd", 123, 2123, 1233),
			new Keyword(12L, "efg", 1543, 21234, 1233));
	}
}
