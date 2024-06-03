package com.depromeet.breadmapbackend.domain.breaddiary;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryRepository;
import com.depromeet.breadmapbackend.domain.breaddiary.dto.AddBreadDiaryDto;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BreadDiaryServiceImpl implements BreadDiaryService {

    private final UserRepository userRepository;

    private final BakeryRepository bakeryRepository;

    private final BreadDiaryRepository breadDiaryRepository;

    @Override
    public void addBreadDiary(AddBreadDiaryDto dto) {
        User user = userRepository.findByOAuthId(dto.oAuthId())
                .orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Bakery bakery = bakeryRepository.findById(dto.bakeryId())
                .orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        BreadDiary breadDiary = new BreadDiary(user, bakery, dto.productName(), dto.productPrice(), dto.rating());
        breadDiaryRepository.save(breadDiary);
    }
}
