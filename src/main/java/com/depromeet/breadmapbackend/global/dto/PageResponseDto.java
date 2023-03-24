package com.depromeet.breadmapbackend.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponseDto<D> {
    private int pageNumber; // 현재 페이지
    private int numberOfElements; // 현재 페이지 데이터 수
    private int size; // 페이지 크기
    private long totalElements; // 전체 데이터 수
    private int totalPages; // 전체 페이지 수
    private List<D> contents;

    public static <E, D> PageResponseDto<D> of(Page<E> entity, Function<E, D> makeDto) {
        List<D> dto = convertToDto(entity, makeDto);
        return new PageResponseDto<> (entity.getNumber(),
                entity.getNumberOfElements(),
                entity.getSize(),
                entity.getTotalElements(),
                entity.getTotalPages(), dto);
    }

    public static <E, D> PageResponseDto<D> of(Page<E> entity, List<D> contents) {
        return new PageResponseDto<> (entity.getNumber(),
                entity.getNumberOfElements(),
                entity.getSize(),
                entity.getTotalElements(),
                entity.getTotalPages(), contents);
    }

    private static <E, D> List<D> convertToDto(Page<E> entity, Function<E, D> makeDto) {
        return entity.getContent().stream()
                .map(e -> makeDto.apply(e))
                .collect(Collectors.toList());
    }
}