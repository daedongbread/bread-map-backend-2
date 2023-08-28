package com.depromeet.breadmapbackend.domain.admin.feed.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.admin.feed.domain.FeedType;

@Component
public class FeedServiceFactory {
	private final Map<FeedType, FeedService> serviceMap = new HashMap<>();

	public FeedServiceFactory(List<FeedService> services) {
		services.forEach(service -> serviceMap.put(service.getServiceType(), service));
	}

	public FeedService getService(FeedType serviceType) {
		return serviceMap.get(serviceType);
	}
}
