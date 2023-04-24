package com.depromeet.breadmapbackend.global.converter.param;

import com.depromeet.breadmapbackend.domain.notice.NoticeDayType;
import org.springframework.core.convert.converter.Converter;

public class NoticeDayTypeConverter implements Converter<String, NoticeDayType> {
    @Override
    public NoticeDayType convert(String param) {
        return NoticeDayType.findByCode(param);
    }
}
