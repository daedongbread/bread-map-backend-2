package com.depromeet.breadmapbackend.domain.flag;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Flag extends BaseEntity {

	private static final List<String> UNEDITABLE_FLAG_NAMES = List.of("가고싶어요", "가봤어요");

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id")
	private User user;

	@OneToMany(mappedBy = "flag", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<FlagBakery> flagBakeryList = new LinkedHashSet<>();

	@Enumerated(value = EnumType.STRING)
	@Column(nullable = false)
	private FlagColor color;

	@Builder
	private Flag(String name, User user, FlagColor color) {
		this.name = name;
		this.user = user;
		this.color = color;
	}

	public void removeFlagBakery(FlagBakery flagBakery) {
		this.flagBakeryList.remove(flagBakery);
	}

	public boolean isEditable() {
		return !UNEDITABLE_FLAG_NAMES.contains(this.name);
	}

	public void updateFlag(String name, FlagColor color) {
		this.name = name;
		this.color = color;
	}
}