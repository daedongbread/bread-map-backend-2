package com.depromeet.breadmapbackend.domain.event;

import com.depromeet.breadmapbackend.domain.breaddiary.BreadDiary;
import com.depromeet.breadmapbackend.global.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public BreadDiaryEvent(BreadDiary breadDiary) {
        this.diaryId = breadDiary.getId();
        this.diary = breadDiary;
    }
}
