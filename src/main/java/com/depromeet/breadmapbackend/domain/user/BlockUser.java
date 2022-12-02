package com.depromeet.breadmapbackend.domain.user;

import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlockUser extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(optional = false)
    private User blockUser;

    @Builder
    public BlockUser(User user, User blockUser) {
        this.user = user;
        this.blockUser = blockUser;
    }
}
