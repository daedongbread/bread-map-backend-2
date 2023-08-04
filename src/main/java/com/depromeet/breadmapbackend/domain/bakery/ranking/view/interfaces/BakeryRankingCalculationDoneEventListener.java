package com.depromeet.breadmapbackend.domain.bakery.ranking.view.interfaces;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import com.depromeet.breadmapbackend.domain.bakery.ranking.view.domain.usecase.BakeryRankViewCreateUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * BakeryRankingCalculationDoneEventListener
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/08/03
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BakeryRankingCalculationDoneEventListener implements
	StreamListener<String, MapRecord<String, String, String>> {

	private final BakeryRankViewCreateUseCase bakeryRankViewCreateUseCase;

	@Override
	public void onMessage(final MapRecord<String, String, String> message) {
		bakeryRankViewCreateUseCase.command();
	}

}
