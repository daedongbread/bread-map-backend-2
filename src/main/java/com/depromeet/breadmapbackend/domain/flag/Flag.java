package com.depromeet.breadmapbackend.domain.flag;

import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import com.depromeet.breadmapbackend.domain.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Flag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "flag", cascade = CascadeType.ALL, orphanRemoval = true)
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

    public void addFlagBakery(FlagBakery flagBakery) {
        this.flagBakeryList.add(flagBakery);
    }

    public void removeFlagBakery(FlagBakery flagBakery) {
        this.flagBakeryList.remove(flagBakery);
    }

//    @Override
//    public boolean equals(Object object) {
//        Flag flag = (Flag) object;
//        if (flag.getName().equals(this.getName()) && flag.getUser() == this.getUser()) {
//            return true;
//        }
//        return false;
//    }
}