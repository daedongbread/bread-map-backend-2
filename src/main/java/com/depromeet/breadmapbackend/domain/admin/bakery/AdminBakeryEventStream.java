package com.depromeet.breadmapbackend.domain.admin.bakery;

import java.util.Map;

import com.depromeet.breadmapbackend.global.DaeDongEvents;

/**
 * AdminBakeryEventStream
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/04
 */
public interface AdminBakeryEventStream {

	void publish(final DaeDongEvents event, final Map<String, String> bakeryId);
}
