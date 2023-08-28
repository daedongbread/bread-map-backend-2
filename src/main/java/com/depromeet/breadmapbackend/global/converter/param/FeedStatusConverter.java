package com.depromeet.breadmapbackend.global.converter.param;

import org.springframework.core.convert.converter.Converter;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedStatus;

public class FeedStatusConverter implements Converter<String, FeedStatus> {

	@Override
	public FeedStatus convert(String source) {
		return FeedStatus.findByCode(source);
	}
}
