package com.depromeet.breadmapbackend.service.flag;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.exception.BakeryNotFoundException;
import com.depromeet.breadmapbackend.domain.flag.Flag;
import com.depromeet.breadmapbackend.domain.flag.exception.*;
import com.depromeet.breadmapbackend.domain.bakery.repository.BakeryRepository;
import com.depromeet.breadmapbackend.domain.flag.FlagBakery;
import com.depromeet.breadmapbackend.domain.flag.repository.FlagBakeryRepository;
import com.depromeet.breadmapbackend.domain.flag.repository.FlagRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.exception.UserNotFoundException;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.web.controller.flag.dto.AddFlagRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlagServiceImpl implements FlagService {
    private final FlagRepository flagRepository;
    private final FlagBakeryRepository flagBakeryRepository;
    private final UserRepository userRepository;
    private final BakeryRepository bakeryRepository;

    @Transactional
    public void addFlag(String username, AddFlagRequest request) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        if(flagRepository.findByUserAndName(user, request.getName()).isPresent()) throw new FlagAlreadyException();

        Flag flag = Flag.builder().user(user).name(request.getName()).color(request.getColor()).build();
        flagRepository.save(flag);
        user.addFlag(flag);
    }

    @Transactional
    public void removeFlag(String username, Long flagId) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Flag flag = flagRepository.findByUserAndId(user, flagId).orElseThrow(FlagNotFoundException::new);
        if(flag.getName().equals("가고싶어요") || flag.getName().equals("가봤어요")) throw new FlagUnRemoveException();

        user.removeFlag(flag);
        flagRepository.delete(flag);
    }

    @Transactional
    public void addBakeryToFlag(String username, Long flagId, Long bakeryId) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(BakeryNotFoundException::new);

        Flag flag = flagRepository.findByUserAndId(user, flagId).orElseThrow(FlagNotFoundException::new);
        FlagBakery flagBakery = FlagBakery.builder().flag(flag).bakery(bakery).build();

        if(flagBakeryRepository.findByFlagAndBakery(flag, bakery).isPresent()) throw new FlagBakeryAlreadyException();
        else {
            if(flag.getName().equals("가봤어요")) bakery.addFlagNum();

            flagBakeryRepository.save(flagBakery);
            flag.addFlagBakery(flagBakery);
        }
    }

    @Transactional
    public void removeBakeryToFlag(String username, Long flagId, Long flagBakeryId) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);
        Flag flag = flagRepository.findByUserAndId(user, flagId).orElseThrow(FlagNotFoundException::new);
        FlagBakery flagBakery = flagBakeryRepository.findById(flagBakeryId).orElseThrow(FlagBakeryNotFoundException::new);
        Bakery bakery = bakeryRepository.findById(flagBakery.getBakery().getId()).orElseThrow(BakeryNotFoundException::new);

        if(flag.getName().equals("가봤어요")) bakery.minusFlagNum();
        flag.removeFlagBakery(flagBakery);
    }
}
