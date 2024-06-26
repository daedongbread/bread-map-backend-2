package com.depromeet.breadmapbackend.domain.event;

import com.depromeet.breadmapbackend.domain.breaddiary.BreadDiary;
import com.depromeet.breadmapbackend.global.BaseEntity;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@ToString
public class BreadDiaryEvent extends BaseEntity {
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
