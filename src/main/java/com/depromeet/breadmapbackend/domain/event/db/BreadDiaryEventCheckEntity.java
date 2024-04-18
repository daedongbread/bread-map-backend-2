package com.depromeet.breadmapbackend.domain.event.db;

import com.depromeet.breadmapbackend.domain.breaddiary.BreadDiary;
import com.depromeet.breadmapbackend.domain.event.domain.breaddiaryevent.BreadDiaryEventCheckState;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Table(name = "bread_diary_event_check")
class BreadDiaryEventCheckEntity extends TimestampEntity {
    @Id
    @GeneratedValue
    private long id;
    @OneToOne
    @JoinColumn(name = "diary_id")
    @NotNull
    private BreadDiary diary;
    @NotNull
    private String description = "";
    @Enumerated(EnumType.STRING)
    @NotNull
    private BreadDiaryEventCheckState state = BreadDiaryEventCheckState.PENDING;
}
