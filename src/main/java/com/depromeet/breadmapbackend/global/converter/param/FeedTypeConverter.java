package com.depromeet.breadmapbackend.global.converter.param;

import org.springframework.core.convert.converter.Converter;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedType;

public class FeedTypeConverter implements Converter<String, FeedType> {
	@Override
	public FeedType convert(String source) {
		return FeedType.findByCode(source);
	}
}
