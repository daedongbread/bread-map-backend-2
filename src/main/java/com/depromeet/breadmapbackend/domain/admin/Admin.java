package com.depromeet.breadmapbackend.domain.admin;

import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import com.depromeet.breadmapbackend.security.domain.RoleType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Admin extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Json 결과로 출력하지 못하게
    @Column(nullable = false)
    private String password;

//    @Column(nullable = false)
//    private String nickName;

    @Enumerated(EnumType.STRING)
    private RoleType roleType = RoleType.ADMIN;
}
