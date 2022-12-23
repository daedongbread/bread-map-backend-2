package com.depromeet.breadmapbackend.web.controller.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


@Slf4j
public class PageableSortConverter {
    public static Pageable convertSort(Pageable pageable) {
        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().map(Sort.Order::getDirection).toList().get(0),
                pageable.getSort().map(sort -> camelToSnake(sort.getProperty())).toList().get(0));
    }

    public static String camelToSnake(String str) {
        // Regular Expression
        String regex = "([a-z])([A-Z]+)";

        // Replacement string
        String replacement = "$1_$2";

        // Replace the given regex
        // with replacement string
        // and convert it to lower case.
        str = str.replaceAll(regex, replacement).toLowerCase();

        // return string
        return str;
    }
}
