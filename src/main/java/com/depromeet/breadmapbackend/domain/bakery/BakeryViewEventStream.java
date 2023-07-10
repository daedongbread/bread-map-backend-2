package com.depromeet.breadmapbackend.domain.bakery;

import java.util.HashMap;
import java.util.Map;

/**
 * BakeryViewEventStream
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/10
 */
public interface BakeryViewEventStream {
	void publish(Map<String, String> fieldMap);

}
