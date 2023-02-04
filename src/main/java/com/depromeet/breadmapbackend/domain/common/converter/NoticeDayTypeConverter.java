package com.depromeet.breadmapbackend.domain.common.converter;

import com.depromeet.breadmapbackend.domain.notice.NoticeDayType;
import org.springframework.core.convert.converter.Converter;

public class NoticeDayTypeConverter implements Converter<String, NoticeDayType> {
    @Override
    public NoticeDayType convert(String str) {
        return NoticeDayType.valueOf(str.toUpperCase());
    }
}
