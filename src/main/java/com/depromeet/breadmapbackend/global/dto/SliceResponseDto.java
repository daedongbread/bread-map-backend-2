package com.depromeet.breadmapbackend.global.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SliceResponseDto <D> {
    private int pageNumber; // 현재 페이지
    private int numberOfElements; // 현재 페이지 데이터 수
    private int size; // 페이지 크기
    private boolean hasNext; // 다음 slice 존재 여부
    private List<D> contents;

    public static <E, D> SliceResponseDto<D> of(Slice<E> entity, List<D> contents) {
        return new SliceResponseDto<> (entity.getNumber(),
                entity.getNumberOfElements(),
                entity.getSize(),
                entity.hasNext(), contents);
    }

    public static <E, D> SliceResponseDto<D> of(Slice<E> entity, Function<E, D> makeDto) {
        List<D> dto = convertToDto(entity, makeDto);
        return new SliceResponseDto<> (entity.getNumber(),
                entity.getNumberOfElements(),
                entity.getSize(),
                entity.hasNext(), dto);
    }

    private static <E, D> List<D> convertToDto(Slice<E> entity, Function<E, D> makeDto) {
        return entity.getContent().stream()
                .map(e -> makeDto.apply(e))
                .collect(Collectors.toList());
    }
}