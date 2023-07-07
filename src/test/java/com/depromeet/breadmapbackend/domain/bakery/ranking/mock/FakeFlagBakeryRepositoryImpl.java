package com.depromeet.breadmapbackend.domain.bakery.ranking.mock;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.ranking.util.FixtureFactory;
import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.domain.flag.FlagBakeryRepository;
import com.depromeet.breadmapbackend.domain.user.User;

/**
 * FakeFlagBakeryRepository
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/03
 */
public class FakeFlagBakeryRepositoryImpl implements FlagBakeryRepository {
	@Override
	public int countFlagNum(final Bakery bakery) {
		return 0;
	}

	@Override
	public List<FlagBakery> findByFlagAndStatusIsPostingOrderByCreatedAtDesc(final Flag flag) {
		return null;
	}

	@Override
	public Optional<FlagBakery> findByBakeryAndFlagAndUser(final Bakery bakery, final Flag flag, final User user) {
		return Optional.empty();
	}

	@Override
	public Optional<FlagBakery> findByBakeryAndUser(final Bakery bakery, final User user) {
		return Optional.empty();
	}

	@Override
	public Optional<Flag> findFlagByBakeryAndUser(final Bakery bakery, final Long userId) {
		return Optional.empty();
	}

	@Override
	public void delete(final FlagBakery flagBakery) {

	}

	@Override
	public void deleteAllById(final Iterable<? extends Long> longs) {

	}

	@Override
	public void deleteAll(final Iterable<? extends FlagBakery> entities) {

	}

	@Override
	public void deleteAll() {

	}

	@Override
	public Optional<FlagBakery> findByBakeryIdAndFlagIdAndUserId(final Long bakeryId, final Long flagId,
		final Long userId) {
		return Optional.empty();
	}

	@Override
	public List<FlagBakery> findByFlagAndUserIdOrderByCreatedAtDesc(final Flag flag, final Long userId) {
		return null;
	}

	@Override
	public List<FlagBakery> findByUserIdAndBakeryIdIn(final Long userId, final List<Long> bakeryIdList) {
		return List.of(FixtureFactory.getFlagBakery(1L, userId).nextObject(FlagBakery.class));
	}

	@Override
	public List<FlagBakery> findAll() {
		return null;
	}

	@Override
	public List<FlagBakery> findAll(final Sort sort) {
		return null;
	}

	@Override
	public Page<FlagBakery> findAll(final Pageable pageable) {
		return null;
	}

	@Override
	public List<FlagBakery> findAllById(final Iterable<Long> longs) {
		return null;
	}

	@Override
	public long count() {
		return 0;
	}

	@Override
	public void deleteById(final Long aLong) {

	}

	@Override
	public <S extends FlagBakery> S save(final S entity) {
		return null;
	}

	@Override
	public <S extends FlagBakery> List<S> saveAll(final Iterable<S> entities) {
		return null;
	}

	@Override
	public Optional<FlagBakery> findById(final Long aLong) {
		return Optional.empty();
	}

	@Override
	public boolean existsById(final Long aLong) {
		return false;
	}

	@Override
	public void flush() {

	}

	@Override
	public <S extends FlagBakery> S saveAndFlush(final S entity) {
		return null;
	}

	@Override
	public <S extends FlagBakery> List<S> saveAllAndFlush(final Iterable<S> entities) {
		return null;
	}

	@Override
	public void deleteAllInBatch(final Iterable<FlagBakery> entities) {

	}

	@Override
	public void deleteAllByIdInBatch(final Iterable<Long> longs) {

	}

	@Override
	public void deleteAllInBatch() {

	}

	@Override
	public FlagBakery getOne(final Long aLong) {
		return null;
	}

	@Override
	public FlagBakery getById(final Long aLong) {
		return null;
	}

	@Override
	public <S extends FlagBakery> Optional<S> findOne(final Example<S> example) {
		return Optional.empty();
	}

	@Override
	public <S extends FlagBakery> List<S> findAll(final Example<S> example) {
		return null;
	}

	@Override
	public <S extends FlagBakery> List<S> findAll(final Example<S> example, final Sort sort) {
		return null;
	}

	@Override
	public <S extends FlagBakery> Page<S> findAll(final Example<S> example, final Pageable pageable) {
		return null;
	}

	@Override
	public <S extends FlagBakery> long count(final Example<S> example) {
		return 0;
	}

	@Override
	public <S extends FlagBakery> boolean exists(final Example<S> example) {
		return false;
	}

	@Override
	public <S extends FlagBakery, R> R findBy(final Example<S> example,
		final Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
		return null;
	}
}
