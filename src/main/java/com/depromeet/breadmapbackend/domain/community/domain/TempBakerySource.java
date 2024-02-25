package com.depromeet.breadmapbackend.domain.community.domain;

import static javax.persistence.GenerationType.*;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * TempBakerySource
 *
 * @author jaypark
 * @version 1.0.0
 * @since 12/20/23
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TempBakerySource {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	private String bakeryName;

	private String bakeryAddress;

	@OneToMany(mappedBy = "tempBakerySource")
	private List<TempProductSource> tempBakery;
}
