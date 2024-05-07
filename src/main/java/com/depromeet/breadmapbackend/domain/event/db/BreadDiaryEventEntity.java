package com.depromeet.breadmapbackend.domain.event.db;

import com.depromeet.breadmapbackend.domain.breaddiary.BreadDiary;
import com.depromeet.breadmapbackend.domain.event.domain.breaddiaryevent.BreadDiaryEventState;
import com.depromeet.breadmapbackend.global.BaseEntity;
import lombok.Getter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Table(name = "bread_diary_event_check")
public class BreadDiaryEventEntity extends BaseEntity {
    @Id
    private long diaryId;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "diary_id")
    @NotNull
    private BreadDiary diary;

    @NotNull
    private String description = "";

    @Enumerated(EnumType.STRING)
    @NotNull
    private BreadDiaryEventState state = BreadDiaryEventState.PENDING;
}
