package com.depromeet.breadmapbackend.service.flag;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.exception.BakeryNotFoundException;
import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.flag.exception.*;
import com.depromeet.breadmapbackend.domain.bakery.repository.BakeryRepository;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.domain.flag.repository.FlagBakeryRepository;
import com.depromeet.breadmapbackend.domain.flag.repository.FlagRepository;
import com.depromeet.breadmapbackend.domain.review.ReviewProductRating;
import com.depromeet.breadmapbackend.domain.review.Review;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.exception.UserNotFoundException;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.web.controller.flag.dto.FlagDto;
import com.depromeet.breadmapbackend.web.controller.flag.dto.FlagRequest;
import com.depromeet.breadmapbackend.web.controller.flag.dto.FlagBakeryCardDto;
import com.depromeet.breadmapbackend.web.controller.flag.dto.SimpleFlagDto;
import com.depromeet.breadmapbackend.web.controller.review.dto.MapSimpleReviewDto;
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
    private final BakeryRepository bakeryRepository;

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<SimpleFlagDto> findSimpleFlags(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        return flagRepository.findByUser(user).stream()
                .map(flag -> SimpleFlagDto.builder()
                        .flagId(flag.getId()).name(flag.getName()).color(flag.getColor()).build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<FlagDto> findFlags(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        return flagRepository.findByUser(user).stream()
                .map(flag -> FlagDto.builder()
                        .flagId(flag.getId()).name(flag.getName()).color(flag.getColor())
                        .bakeryImageList(flagBakeryRepository.findBakeryByFlag(flag).stream()
                                .sorted(Comparator.comparing(Bakery::getId).reversed()).map(Bakery::getImage)
                                .limit(3).collect(Collectors.toList())).build())
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addFlag(String username, FlagRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        if(flagRepository.findByUserAndName(user, request.getName()).isPresent()) throw new FlagAlreadyException();

        Flag flag = Flag.builder().user(user).name(request.getName()).color(request.getColor()).build();
        flagRepository.save(flag);
        user.addFlag(flag);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeFlag(String username, Long flagId) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Flag flag = flagRepository.findByUserAndId(user, flagId).orElseThrow(FlagNotFoundException::new);
        if(flag.getName().equals("가고싶어요") || flag.getName().equals("가봤어요")) throw new FlagUnEditException();

        user.removeFlag(flag);
        flagRepository.delete(flag);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateFlag(String username, Long flagId, FlagRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Flag flag = flagRepository.findByUserAndId(user, flagId).orElseThrow(FlagNotFoundException::new);

        if(flag.getName().equals("가고싶어요") || flag.getName().equals("가봤어요")) throw new FlagUnEditException();

        flag.updateFlag(request.getName(), request.getColor());
    }

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public List<FlagBakeryCardDto> findBakeryByFlag(String username, Long flagId) {
        Flag flag = flagRepository.findById(flagId).orElseThrow(FlagNotFoundException::new);
        return flagBakeryRepository.findByFlag(flag).stream()
                .sorted(Comparator.comparing(FlagBakery::getId).reversed())
                .map(flagBakery -> FlagBakeryCardDto.builder()
                        .bakery(flagBakery.getBakery())
//                        .rating(Math.floor(Arrays.stream(flagBakery.getBakery().getBreadReviewList().stream().map(BreadReview::getRating)
//                                .mapToInt(Integer::intValue).toArray()).average().orElse(0)*10)/10.0)
                        .rating(Math.floor(Arrays.stream(flagBakery.getBakery().getReviewList()
                                .stream().map(br -> {
                                    return Arrays.stream(br.getRatings().stream().map(ReviewProductRating::getRating).mapToLong(Long::longValue).toArray()).average().orElse(0)*10/10.0;
                                }).collect(Collectors.toList()).stream().mapToLong(Double::longValue).toArray()).average().orElse(0)*10/10.0))
                        .reviewNum(flagBakery.getBakery().getReviewList().size())
                        .simpleReviewList(flagBakery.getBakery().getReviewList().stream()
                                .sorted(Comparator.comparing(Review::getId).reversed()).map(MapSimpleReviewDto::new)
                                .limit(3).collect(Collectors.toList())).build())
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public void addBakeryToFlag(String username, Long flagId, Long bakeryId) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(BakeryNotFoundException::new);

        Flag flag = flagRepository.findById(flagId).orElseThrow(FlagNotFoundException::new);

        if(flagBakeryRepository.findByBakeryAndFlagAndUser(bakery, flag, user).isPresent()) throw new FlagBakeryAlreadyException();

        if(flagBakeryRepository.findByBakeryAndUser(bakery, user).isPresent()) {
            FlagBakery flagBakery = flagBakeryRepository.findByBakeryAndUser(bakery, user).get();
            if(flagBakery.getFlag().getName().equals("가봤어요")) bakery.minusFlagNum();
            flagBakery.getFlag().removeFlagBakery(flagBakery);
            flagBakeryRepository.delete(flagBakery); // TODO : 수정 가능할듯
        }

        FlagBakery flagBakery = FlagBakery.builder()
                .flag(flag).bakery(bakery).user(user).color(flag.getColor()).build();
        if(flag.getName().equals("가봤어요")) bakery.addFlagNum();
        flagBakeryRepository.save(flagBakery);
        flag.addFlagBakery(flagBakery);
    }

    @Transactional(rollbackFor = Exception.class)
    public void removeBakeryToFlag(String username, Long flagId, Long flagBakeryId) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Flag flag = flagRepository.findByUserAndId(user, flagId).orElseThrow(FlagNotFoundException::new);
        FlagBakery flagBakery = flagBakeryRepository.findById(flagBakeryId).orElseThrow(FlagBakeryNotFoundException::new);
        Bakery bakery = bakeryRepository.findById(flagBakery.getBakery().getId()).orElseThrow(BakeryNotFoundException::new);

        if(flag.getName().equals("가봤어요")) bakery.minusFlagNum();
        flag.removeFlagBakery(flagBakery);
    }
}
