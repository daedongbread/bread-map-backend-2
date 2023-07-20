package com.depromeet.breadmapbackend.domain.admin.bakery;

import static org.jeasy.random.FieldPredicates.*;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Predicate;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import com.depromeet.breadmapbackend.domain.admin.bakery.dto.BakeryAddRequest;

/**
 * FixtureFactory
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/17
 */
public class FixtureFactory {
	public static EasyRandom getBakeryAddDto(final Long reportId) {
		Predicate<Field> idPredicate = named("reportId").and(ofType(Long.class))
			.and(inClass(BakeryAddRequest.class));

		Predicate<Field> productAddRequestPredicate = named("productList").and(ofType(List.class))
			.and(inClass(BakeryAddRequest.class));

		EasyRandomParameters param = new EasyRandomParameters()
			.stringLengthRange(3, 20)
			.randomize(idPredicate, () -> reportId)
			.randomize(productAddRequestPredicate, () -> null);
		return new EasyRandom(param);
	}

}