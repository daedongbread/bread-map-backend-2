package com.depromeet.breadmapbackend.domain.challenge.breaddiary;

import com.depromeet.breadmapbackend.domain.challenge.breaddiary.dto.AddBreadDiaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BreadDiaryServiceImpl implements BreadDiaryService {

    private final BreadDiaryRepository breadDiaryRepository;

    @Override
    public void addBreadDiary(AddBreadDiaryDto dto) {

    }
}
