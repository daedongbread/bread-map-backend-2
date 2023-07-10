package com.depromeet.breadmapbackend.domain.bakery.view;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BakeryViewId implements Serializable {

	@EqualsAndHashCode.Include
	private Long bakeryId;
	@EqualsAndHashCode.Include
	private LocalDate viewDate;

}
