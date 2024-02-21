package com.depromeet.breadmapbackend.domain.community.domain;

import static javax.persistence.FetchType.LAZY;
import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.converter.BooleanToYNConverter;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Community
 *
 * @author jaypark
 * @version 1.0.0
 * @since 12/20/23
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "communityType")
public abstract class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private CommunityType communityType;

    @Column(nullable = false, length = 500)
    private String content;

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityImage> images = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "bakery_id")
    private Bakery bakery;

    @Column(nullable = false)
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean isBlock = Boolean.FALSE;

    @Column(nullable = false)
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean isHide = Boolean.FALSE;

    @Column(nullable = false)
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean isDelete = Boolean.FALSE;

    protected Community(
        final User user,
        final CommunityType communityType,
        final String content,
        final Bakery bakery
    ) {
        this.user = user;
        this.communityType = communityType;
        this.content = content;
        this.bakery = bakery;
    }

    public void addImages(final List<String> images) {
        images.forEach(image -> this.images.add(CommunityImage.createCommunityImage(this, image)));
    }
}
