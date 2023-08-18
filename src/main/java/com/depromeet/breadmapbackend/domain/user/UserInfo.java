package com.depromeet.breadmapbackend.domain.user;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
public class UserInfo {
	@Column(nullable = false, unique = true)
	private String nickName;

	private String email;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	private String image;

	public void updateNickName(String nickName) {
		this.nickName = nickName;
	}

	public void updateImage(String image) {
		this.image = image;
	}

	public void updateEmail(String email) {
		this.email = email;
	}

	private UserInfo(
		final String email,
		final String image
	) {
		this.nickName = createNickName();
		this.email = email;
		this.gender = null;
		this.image = image;
	}

	public static UserInfo create(
		final String email,
		final String image
	) {
		return new UserInfo(email, image);
	}

	public UserInfo updateDefaultNickName() {
		this.nickName = createNickName();
		return this;
	}

	private String createNickName() {
		List<String> adjectiveList =
			Arrays.asList("맛있는", "달콤한", "매콤한", "바삭한", "짭잘한", "고소한", "알싸한", "새콤한", "느끼한", "무서운");
		List<String> breadNameList =
			Arrays.asList("식빵", "소금빵", "바게트", "마카롱", "마늘빵", "베이글", "도넛", "꽈배기", "피자빵", "크루아상");

		String adjective = adjectiveList.get(new SecureRandom().nextInt(adjectiveList.size()));
		String breadName = breadNameList.get(new SecureRandom().nextInt(breadNameList.size()));

		int num = new SecureRandom().nextInt(9999) + 1;

		return adjective + breadName + num;
	}
}
