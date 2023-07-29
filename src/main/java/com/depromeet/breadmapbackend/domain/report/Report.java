package com.depromeet.breadmapbackend.domain.report;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.global.BaseEntity;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Report
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/25
 */

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Report extends BaseEntity {

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long postId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ReportType reportType;

	@ManyToOne(fetch = LAZY)
	@JoinColumn(name = "reporter_id")
	private User reporter;

	@Column(name = "reporter_id", insertable = false, updatable = false)
	private Long reporterId;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private ReportReason reportReason;

	private String content;

	@Builder
	public Report(
		final Long postId,
		final Long reporterId,
		final ReportReason reportReason,
		final String content,
		final ReportType reportType
	) {
		if (
			reportReason == ReportReason.ETC &&
				(content.length() < 10 || content.length() > 200)
		)
			throw new DaedongException(DaedongStatus.REPORT_CONTENT_EXCEPTION);
		this.postId = postId;
		this.reporterId = reporterId;
		this.reportReason = reportReason;
		this.content = content;
		this.reportType = reportType;
	}
}
