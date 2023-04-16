package com.depromeet.breadmapbackend.domain.flag;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.user.block.BlockUserRepository;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import com.depromeet.breadmapbackend.domain.bakery.BakeryRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRating;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.domain.flag.dto.FlagDto;
import com.depromeet.breadmapbackend.domain.flag.dto.FlagRequest;
import com.depromeet.breadmapbackend.domain.flag.dto.FlagBakeryDto;
import com.depromeet.breadmapbackend.domain.review.dto.MapSimpleReviewDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlagServiceImpl implements FlagService {
    private final FlagRepository flagRepository;
    private final FlagBakeryRepository flagBakeryRepository;
    private final UserRepository userRepository;
    private final BlockUserRepository blockUserRepository;
    private final BakeryRepository bakeryRepository;

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<FlagDto> getFlags(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        return flagRepository.findByUser(user).stream()
                .map(flag -> FlagDto.builder()
                        .flag(flag)
                        .bakeryImageList(flag.getFlagBakeryList().stream()
                                .sorted(Comparator.comparing(FlagBakery::getCreatedAt)).limit(3)
                                .map(flagBakery -> flagBakery.getBakery().getImage())
                                .collect(Collectors.toList())).build())
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addFlag(String oAuthId, FlagRequest request) {
        User user = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        if(flagRepository.findByUserAndName(user, request.getName()).isPresent()) throw new DaedongException(DaedongStatus.FLAG_DUPLICATE_EXCEPTION);
        if(request.getColor().equals(FlagColor.GRAY)) throw new DaedongException(DaedongStatus.FLAG_COLOR_EXCEPTION);

        Flag flag = Flag.builder().user(user).name(request.getName()).color(request.getColor()).build();
        flagRepository.save(flag);
        user.getFlagList().add(flag);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateFlag(String oAuthId, Long flagId, FlagRequest request) {
        User user = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Flag flag = flagRepository.findByUserAndId(user, flagId).orElseThrow(() -> new DaedongException(DaedongStatus.FLAG_NOT_FOUND));

        if(flag.getName().equals("가고싶어요") || flag.getName().equals("가봤어요")) throw new DaedongException(DaedongStatus.FLAG_UNEDIT_EXCEPTION);
        if(request.getColor().equals(FlagColor.GRAY)) throw new DaedongException(DaedongStatus.FLAG_COLOR_EXCEPTION);

        flag.updateFlag(request.getName(), request.getColor());
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeFlag(String oAuthId, Long flagId) {
        User user = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Flag flag = flagRepository.findByUserAndId(user, flagId).orElseThrow(() -> new DaedongException(DaedongStatus.FLAG_NOT_FOUND));
        if(flag.getName().equals("가고싶어요") || flag.getName().equals("가봤어요")) throw new DaedongException(DaedongStatus.FLAG_UNEDIT_EXCEPTION);

        user.getFlagList().remove(flag);
        flagRepository.delete(flag);
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public FlagBakeryDto getBakeryByFlag(String oAuthId, Long flagId) { // TODO page?
        User me = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Flag flag = flagRepository.findById(flagId).orElseThrow(() -> new DaedongException(DaedongStatus.FLAG_NOT_FOUND));
        return FlagBakeryDto.builder().flag(flag)
                .flagBakeryInfoList(
                        flagBakeryRepository.findByFlag(flag).stream()
                                .sorted(Comparator.comparing(FlagBakery::getCreatedAt).reversed())
                                .map(flagBakery -> FlagBakeryDto.FlagBakeryInfo.builder()
                                        .bakery(flagBakery.getBakery())
                                        .rating(Math.floor(Arrays.stream(flagBakery.getBakery().getReviewList()
                                                .stream()
                                                .filter(review -> blockUserRepository.findByFromUserAndToUser(me, review.getUser()).isEmpty())
                                                .map(br -> {
                                                    return Arrays.stream(br.getRatings().stream().map(ReviewProductRating::getRating).mapToLong(Long::longValue).toArray()).average().orElse(0)*10/10.0;
                                                }).collect(Collectors.toList()).stream().mapToLong(Double::longValue).toArray()).average().orElse(0)*10/10.0))
                                        .reviewNum((int) flagBakery.getBakery().getReviewList().stream()
                                                .filter(review -> blockUserRepository.findByFromUserAndToUser(me, review.getUser()).isEmpty()).count())
                                        .simpleReviewList(flagBakery.getBakery().getReviewList().stream()
                                                .filter(review -> blockUserRepository.findByFromUserAndToUser(me, review.getUser()).isEmpty())
                                                .sorted(Comparator.comparing(Review::getCreatedAt).reversed()).map(MapSimpleReviewDto::new)
                                                .limit(3).collect(Collectors.toList())).build())
                        .collect(Collectors.toList())).build();
    }

    @Transactional(rollbackFor = Exception.class)
    public void addBakeryToFlag(String oAuthId, Long flagId, Long bakeryId) {
        User user = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));

        Flag flag = flagRepository.findById(flagId).orElseThrow(() -> new DaedongException(DaedongStatus.FLAG_NOT_FOUND));

        if(flagBakeryRepository.findByBakeryAndFlagAndUser(bakery, flag, user).isPresent()) throw new DaedongException(DaedongStatus.FLAG_BAKERY_DUPLICATE_EXCEPTION);

        if(flagBakeryRepository.findByBakeryAndUser(bakery, user).isPresent()) {
            FlagBakery flagBakery = flagBakeryRepository.findByBakeryAndUser(bakery, user).get();
            if(flagBakery.getFlag().getName().equals("가봤어요")) bakery.minusFlagNum();
            flagBakery.getFlag().removeFlagBakery(flagBakery);
            flagBakeryRepository.delete(flagBakery); // TODO : 수정 가능할듯
        }

        FlagBakery.builder().flag(flag).bakery(bakery).user(user).build();
        if(flag.getName().equals("가봤어요")) bakery.addFlagNum();
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBakeryToFlag(String oAuthId, Long flagId, Long bakeryId) {
        User user = userRepository.findByOAuthId(oAuthId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        Flag flag = flagRepository.findByUserAndId(user, flagId).orElseThrow(() -> new DaedongException(DaedongStatus.FLAG_NOT_FOUND));
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));
        FlagBakery flagBakery = flagBakeryRepository.findByBakeryAndFlagAndUser(bakery, flag, user).orElseThrow(() -> new DaedongException(DaedongStatus.BAKERY_NOT_FOUND));

        if(flag.getName().equals("가봤어요")) bakery.minusFlagNum();
        flag.removeFlagBakery(flagBakery);
        flagBakeryRepository.delete(flagBakery);
    }
}
