package com.depromeet.breadmapbackend.domain.breaddiary;

import com.depromeet.breadmapbackend.domain.breaddiary.dto.AddBreadDiaryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BreadDiaryServiceImpl implements BreadDiaryService {

    private final BreadDiaryRepository breadDiaryRepository;

    @Override
    public void addBreadDiary(AddBreadDiaryDto dto) {
        BreadDiary breadDiary = new BreadDiary();

    }
}
