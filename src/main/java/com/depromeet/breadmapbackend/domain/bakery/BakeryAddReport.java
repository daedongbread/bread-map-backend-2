package com.depromeet.breadmapbackend.domain.bakery;

import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import com.depromeet.breadmapbackend.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BakeryAddReport extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    private String content;

    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer status;

    @Column(nullable = false, columnDefinition = "boolean default 1")
    private boolean isUse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public BakeryAddReport(String name, String location, String content, User user) {
        this.name = name;
        this.location = location;
        this.content = content;
        this.status = 0;
        this.isUse = true;
        this.user = user;
    }

    public void updateStatus(Integer status) { this.status = status; }
}
