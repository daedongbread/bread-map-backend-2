package com.depromeet.breadmapbackend.domain.bakery;

public record CoordinateRange(
	Double leftLatitude,
	Double rightLatitude,
	Double downLongitude,
	Double upLongitude) {

	public static CoordinateRange of(
		final Double latitude,
		final Double latitudeDelta,
		final Double longitude,
		final Double longitudeDelta
	) {
		return new CoordinateRange(
			getFromCoordinate(latitude, latitudeDelta),
			getToCoordinate(latitude, latitudeDelta),
			getFromCoordinate(longitude, longitudeDelta),
			getToCoordinate(longitude, longitudeDelta)
		);
	}

	private static double getToCoordinate(final Double coordinate, final Double coordinateDelta) {
		return coordinate + coordinateDelta / 2;
	}

	private static double getFromCoordinate(final Double coordinate, final Double coordinateDelta) {
		return coordinate - coordinateDelta / 2;
	}

}