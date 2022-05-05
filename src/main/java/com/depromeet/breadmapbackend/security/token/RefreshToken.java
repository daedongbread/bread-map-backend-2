package com.depromeet.breadmapbackend.security.token;

import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String token;

    @Builder
    public RefreshToken(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public void updateToken(String token) {
        this.token = token;
    }
}
