package com.depromeet.breadmapbackend.domain.breaddiary;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.BakeryRepository;
import com.depromeet.breadmapbackend.domain.breaddiary.dto.AddBreadDiaryDto;
import com.depromeet.breadmapbackend.domain.event.BreadDiaryEvent;
import com.depromeet.breadmapbackend.domain.event.BreadDiaryEventRepository;
import com.depromeet.breadmapbackend.domain.event.UserPoint;
import com.depromeet.breadmapbackend.domain.event.UserPointRepository;
import com.depromeet.breadmapbackend.domain.image.ImageService;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional
public class BreadDiaryServiceImpl implements BreadDiaryService {

    private final ImageService imageService;

    private final UserRepository userRepository;

    private final BakeryRepository bakeryRepository;

    private final BreadDiaryRepository breadDiaryRepository;

    private final BreadDiaryEventRepository breadDiaryEventRepository;

    private final UserPointRepository userPointRepository;

    @Override
    public void addBreadDiary(AddBreadDiaryDto dto) {
        User user = userRepository.findByOAuthId(dto.oAuthId())
                .orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Bakery bakery = bakeryRepository.findById(dto.bakeryId())
                .orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        try {
            imageService.uploadImage(dto.image());
        } catch (IOException e) {
            throw new DaedongException(DaedongStatus.IMAGE_INVALID_EXCEPTION);
        }
        BreadDiary saved = breadDiaryRepository.save(new BreadDiary(user, bakery, dto.productName(), dto.productPrice(), dto.rating()));
        breadDiaryEventRepository.save(new BreadDiaryEvent(saved));
        UserPoint userPoint = userPointRepository.findById(user.getId())
                .orElseGet(() -> userPointRepository.save(new UserPoint(user)));
        userPoint.addPoint(500);
    }
}
