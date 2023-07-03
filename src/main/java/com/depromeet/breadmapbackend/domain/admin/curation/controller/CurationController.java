package com.depromeet.breadmapbackend.domain.admin.curation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.depromeet.breadmapbackend.domain.admin.curation.domain.CurationContext;
import com.depromeet.breadmapbackend.domain.admin.curation.dto.CurationBakeryIds;
import com.depromeet.breadmapbackend.domain.admin.curation.service.CurationService;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/curation")
public class CurationController {

	private final CurationService curationService;
	private final List<CurationContext> curationContext;

	// 전체 큐레이션을 조회한다
	@GetMapping("/all")
	public ResponseEntity readAllCuration() {
		curationService.getAll();
	}

	@GetMapping("/{curationId}")
	public ResponseEntity readCarouselFeed(
		@PathVariable Long curationId,
		@RequestParam(defaultValue = "CURATION") String curationType
	) {
		CurationContext context = curationContext.stream()
			.filter(c -> c.support(curationType))
			.findAny()
			.orElseThrow(() -> new DaedongException(DaedongStatus.CURATION_CONTEXT_NOT_MATCHING));

		context.execute(curationId);
	}

	// 큐레이션을 생성한다
	@PostMapping("/{curationId}")

	// 전체 빵집 리스트를 출력한다(이건 이미 있음)

	// 큐레이션에 추가할 빵집을 최대 5개까지 선택해 큐레이션에 추가한다.

	// 큐레이션을 삭제한다.
	@DeleteMapping("/{curationId}")
	public ResponseEntity<Void> deleteCuration(@PathVariable Long curationId) {
		curationService.removeCuration(curationId);

		return ResponseEntity.noContent().build();
	}

	// 큐레이션을 수정한다.(기본 정보만 수정 가능)

	// 큐레이션베이커리를 수정 (삽입 or 삭제) 한다.
	@PatchMapping("/{curationId}")
	public ResponseEntity<Void> addCurationBakeries(
		@PathVariable Long curationId,
		@RequestBody CurationBakeryIds curationBakeryIds
	) {
		curationService.updateExhibits(curationId, curationBakeryIds.getIds());

		return ResponseEntity.noContent().build();
	}
}
