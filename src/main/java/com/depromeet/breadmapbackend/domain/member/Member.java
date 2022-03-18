package com.depromeet.breadmapbackend.domain.member;

import com.depromeet.breadmapbackend.domain.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

/*
 * Created by ParkSuHo by 2022/03/18.
 */
@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "members")
public class Member extends BaseEntity {
}
