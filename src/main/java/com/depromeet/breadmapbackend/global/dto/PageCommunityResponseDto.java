package com.depromeet.breadmapbackend.global.dto;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageCommunityResponseDto<D> {
	private int pageNumber; // 현재 페이지
	private int numberOfElements; // 현재 페이지 데이터 수
	private int size; // 페이지 크기
	private long totalElements; // 전체 데이터 수
	private int totalPages; // 전체 페이지 수
	private List<D> contents;
	private Long postOffset;
	private Long reviewOffset;

	public static <E, D> PageCommunityResponseDto<D> of(
		final Page<E> entity,
		final Function<E, D> makeDto,
		final Long lastPostId,
		final Long lastReviewId
	) {
		List<D> dto = convertToDto(entity, makeDto);
		return new PageCommunityResponseDto<>(entity.getNumber(),
			entity.getNumberOfElements(),
			entity.getSize(),
			entity.getTotalElements(),
			entity.getTotalPages(),
			dto,
			lastPostId,
			lastReviewId
		);
	}

	private static <E, D> List<D> convertToDto(Page<E> entity, Function<E, D> makeDto) {
		return entity.getContent().stream()
			.map(e -> makeDto.apply(e))
			.collect(Collectors.toList());
	}
}