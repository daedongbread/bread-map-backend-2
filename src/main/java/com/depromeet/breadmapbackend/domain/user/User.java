package com.depromeet.breadmapbackend.domain.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.notice.token.NoticeToken;
import com.depromeet.breadmapbackend.global.BaseEntity;
import com.depromeet.breadmapbackend.global.converter.BooleanToYNConverter;
import com.depromeet.breadmapbackend.global.security.domain.RoleType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Embedded
	private OAuthInfo oAuthInfo;

	@Embedded
	private UserInfo userInfo;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private final RoleType roleType = RoleType.USER;

	@Column(nullable = false)
	@Convert(converter = BooleanToYNConverter.class)
	private Boolean isBlock = Boolean.FALSE;

	@Column(nullable = false)
	@Convert(converter = BooleanToYNConverter.class)
	private Boolean isMarketingInfoReceptionAgreed;

	@Column(nullable = false)
	@Convert(converter = BooleanToYNConverter.class)
	private Boolean isAlarmOn = Boolean.FALSE;

	@JsonIgnore
	@Builder.Default
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Flag> flagList = new ArrayList<>();

	@Column(nullable = false)
	private boolean isDeRegistered = false;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<NoticeToken> noticeTokens = new ArrayList<>();

	private LocalDateTime lastAccessedAt = LocalDateTime.now();

	public void updateLastAccessedAt() {
		this.lastAccessedAt = LocalDateTime.now();
	}

	public String getOAuthId() {
		return this.oAuthInfo.getOAuthId();
	}

	public String getNickName() {
		return this.userInfo.getNickName();
	}

	public void changeBlock() {
		this.isBlock = !this.isBlock;
	}

	public boolean alarmOn() {
		this.isAlarmOn = true;
		return true;
	}

	public boolean alarmOff() {
		this.isAlarmOn = false;
		return false;
	}

	public void deRegisterUser(final String deRegiKeyString, final String defaultImage) {
		this.isDeRegistered = true;
		this.oAuthInfo.deRegister(deRegiKeyString);
		this.userInfo.deRegister(deRegiKeyString, defaultImage);
	}
}
