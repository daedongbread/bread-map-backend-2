package com.depromeet.breadmapbackend.domain.community.domain.service;

import com.depromeet.breadmapbackend.domain.bakery.BakeryRepository;
import com.depromeet.breadmapbackend.domain.community.domain.CommunityType;
import com.depromeet.breadmapbackend.domain.community.domain.dto.BaseCommunityRegisterCommand;
import com.depromeet.breadmapbackend.domain.community.domain.dto.CommunityWithOutProductsRegisterCommand;
import com.depromeet.breadmapbackend.domain.community.domain.extend.CommunityWithOutProduct;
import com.depromeet.breadmapbackend.domain.community.repository.CommunityWithOutProductRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * CommunityBakingService
 *
 * @author jaypark
 * @version 1.0.0
 * @since 12/21/23
 */
@Service
@RequiredArgsConstructor
public class CommunityWithOutProductService implements CommunityService {

    private final CommunityWithOutProductRepository communityWithOutProductRepository;
    private final UserRepository userRepository;
    private final BakeryRepository bakeryRepository;

    @Override
    public boolean support(final CommunityType communityType) {
        return communityType == CommunityType.BAKING;
    }

    @Override
    public void register(BaseCommunityRegisterCommand command, Long userId) {
        if (!(command instanceof CommunityWithOutProductsRegisterCommand castedCommand)) {
            throw new DaedongException(DaedongStatus.INVALID_COMMUNITY_TYPE);
        }
        final User user = userRepository.findById(userId)
            .orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));

        communityWithOutProductRepository.save(
            CommunityWithOutProduct.createCommunityBaking(
                user,
                castedCommand.getCommunityType(),
                castedCommand.getTitle(),
                castedCommand.getContent(),
                castedCommand.getImages(),
                castedCommand.getBakeryId() != null ? bakeryRepository.findById(command.getBakeryId())
                    .orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND)) : null)
        );
    }

}
