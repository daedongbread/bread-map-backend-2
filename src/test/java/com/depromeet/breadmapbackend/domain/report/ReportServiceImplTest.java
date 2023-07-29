package com.depromeet.breadmapbackend.domain.report;

import static org.assertj.core.api.Assertions.*;

import java.sql.Connection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import com.depromeet.breadmapbackend.domain.report.dto.ReportCommand;

/**
 * ReportServiceImplTest
 *
 * @author jaypark
 * @version 1.0.0
 * @since 2023/07/25
 */
class ReportServiceImplTest extends ReportServiceTest {
	@Autowired
	private ReportServiceImpl sut;

	@Autowired
	private EntityManager em;

	@Autowired
	private DataSource dataSource;

	@BeforeEach
	void setUp() throws Exception {
		try (final Connection connection = dataSource.getConnection()) {
			ScriptUtils.executeSqlScript(connection, new ClassPathResource("report-test-data.sql"));
		}
	}

	@Test
	void 신고() throws Exception {
		//given
		final ReportCommand ReviewReportCommand = new ReportCommand(
			ReportType.REVIEW,
			111L,
			ReportReason.UNFIT_CONTENT,
			null
		);
		final ReportCommand CommentReportCommand = new ReportCommand(
			ReportType.COMMENT,
			111L,
			ReportReason.ETC,
			"feefefeffeefefefefefawefawefasdfawef"
		);
		final ReportCommand breadStoryReportCommand = new ReportCommand(
			ReportType.BREAD_STORY,
			222L,
			ReportReason.COPYRIGHT_THEFT,
			null
		);
		final ReportCommand inValidBreadStoryReportCommand = new ReportCommand(
			ReportType.BREAD_STORY,
			222L,
			ReportReason.ETC,
			"efef"
		);
		final ReportCommand inValidUserBreadStoryReportCommand = new ReportCommand(
			ReportType.BREAD_STORY,
			222L,
			ReportReason.ETC,
			"efef"
		);

		//when
		sut.report(ReviewReportCommand, 112L);
		sut.report(CommentReportCommand, 112L);
		sut.report(breadStoryReportCommand, 111L);
		assertThatThrownBy(() -> sut.report(inValidBreadStoryReportCommand, 111L));
		assertThatThrownBy(() -> sut.report(inValidUserBreadStoryReportCommand, 112L));

		//then
		final List<Report> results = em.createQuery("select r from Report r ", Report.class)
			.getResultList();

		assertThat(results).hasSize(3);
		assertThat(results.get(0).getReportType()).isEqualTo(ReportType.REVIEW);
		assertThat(results.get(0).getReportReason()).isEqualTo(ReportReason.UNFIT_CONTENT);
		assertThat(results.get(0).getContent()).isNull();

		assertThat(results.get(1).getReportType()).isEqualTo(ReportType.COMMENT);
		assertThat(results.get(1).getReportReason()).isEqualTo(ReportReason.ETC);
		assertThat(results.get(1).getContent()).isEqualTo("feefefeffeefefefefefawefawefasdfawef");

		assertThat(results.get(2).getReportType()).isEqualTo(ReportType.BREAD_STORY);
		assertThat(results.get(2).getReportReason()).isEqualTo(ReportReason.COPYRIGHT_THEFT);
	}

}