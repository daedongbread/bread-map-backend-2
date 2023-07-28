package com.depromeet.breadmapbackend.global.infra;

import java.util.List;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.data.redis.stream.Cancelable;
import org.springframework.data.redis.stream.Subscription;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SpringShutDownListener
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/28
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionCloser implements ApplicationListener<ContextClosedEvent> {
	private final List<Subscription> streamEventSubscriptions;

	@Override
	public void onApplicationEvent(final ContextClosedEvent event) {
		log.info("========= closing stream event subscription =========");
		streamEventSubscriptions.forEach(Cancelable::cancel);
		log.info("========= closed stream event subscription =========");
	}

}
