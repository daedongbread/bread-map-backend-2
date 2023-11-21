package com.depromeet.breadmapbackend.domain.subway;

import com.depromeet.breadmapbackend.global.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "SUBWAY_STATION",
        indexes = {
                @Index(name = "subway_station_idx1", columnList = "name"),
                @Index(name = "subway_station_idx2", columnList = "latitude, longitude")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "subway_station_uk1", columnNames = {"line", "name"})
        }
)
public class SubwayStation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String line;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

}
