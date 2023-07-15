package com.depromeet.breadmapbackend.domain.bakery.ranking.util;

import static org.jeasy.random.FieldPredicates.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.function.Predicate;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.ranking.ScoredBakery;
import com.depromeet.breadmapbackend.domain.bakery.view.BakeryView;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.domain.user.User;

/**
 * ScoreBakeryFixture
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/03
 */
public class FixtureFactory {

	public static EasyRandom getUser(final Long userId) {
		Predicate<Field> idPredicate = named("id").and(ofType(Long.class))
			.and(inClass(User.class));

		EasyRandomParameters param = new EasyRandomParameters()
			.randomize(idPredicate, () -> userId);
		return new EasyRandom(param);
	}

	public static EasyRandom getFlagBakery(final Long bakeryId, final Long userId) {
		final User randomUser = getUser(userId).nextObject(User.class);
		Predicate<Field> userPredicate = named("user").and(ofType(User.class))
			.and(inClass(FlagBakery.class));

		final Bakery randomBakery = getBakery(bakeryId).nextObject(Bakery.class);
		Predicate<Field> bakeryPredicate = named("bakery").and(ofType(Bakery.class))
			.and(inClass(FlagBakery.class));

		EasyRandomParameters param = new EasyRandomParameters()
			.randomize(userPredicate, () -> randomUser)
			.randomize(bakeryPredicate, () -> randomBakery);
		return new EasyRandom(param);
	}

	public static EasyRandom getBakery(final Long bakeryId) {
		Predicate<Field> idPredicate = named("id").and(ofType(Long.class))
			.and(inClass(Bakery.class));

		EasyRandomParameters param = new EasyRandomParameters()
			.randomize(idPredicate, () -> bakeryId);
		return new EasyRandom(param);
	}

	public static EasyRandom getScoredBakery(final Long bakeryId) {
		final Bakery randomBakery = getBakery(bakeryId).nextObject(Bakery.class);
		Predicate<Field> bakeryPredicate = named("bakery").and(ofType(Bakery.class))
			.and(inClass(ScoredBakery.class));

		Predicate<Field> calculatedDate = named("calculatedDate").and(ofType(LocalDate.class))
			.and(inClass(ScoredBakery.class));

		EasyRandomParameters param = new EasyRandomParameters()
			.randomize(bakeryPredicate, () -> randomBakery)
			.randomize(calculatedDate, LocalDate::now);
		return new EasyRandom(param);
	}

	public static EasyRandom getYesterdayScoredBakery(final Long bakeryId) {
		final Bakery randomBakery = getBakery(bakeryId).nextObject(Bakery.class);
		Predicate<Field> bakeryPredicate = named("bakery").and(ofType(Bakery.class))
			.and(inClass(ScoredBakery.class));

		Predicate<Field> calculatedDate = named("calculatedDate").and(ofType(LocalDate.class))
			.and(inClass(ScoredBakery.class));

		EasyRandomParameters param = new EasyRandomParameters()
			.randomize(bakeryPredicate, () -> randomBakery)
			.randomize(calculatedDate, () -> LocalDate.now().minusDays(1));
		return new EasyRandom(param);
	}

	public static EasyRandom getBakeryView(final Long bakeryId) {
		Predicate<Field> bakeryPredicate = named("bakeryId").and(ofType(Long.class))
			.and(inClass(BakeryView.class));

		EasyRandomParameters param = new EasyRandomParameters()
			.randomize(bakeryPredicate, () -> bakeryId )
			.dateRange(LocalDate.now().minusDays(6), LocalDate.now());

		return new EasyRandom(param);
	}
}
