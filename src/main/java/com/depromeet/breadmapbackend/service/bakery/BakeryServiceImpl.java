package com.depromeet.breadmapbackend.service.bakery;

import com.depromeet.breadmapbackend.domain.bakery.repository.BakeryRepository;
import com.depromeet.breadmapbackend.domain.bakery.repository.BakeryRepositorySupport;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryCardDto;
import com.depromeet.breadmapbackend.web.controller.bakery.dto.BakeryDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BakeryServiceImpl implements BakeryService {
    private final BakeryRepository bakeryRepository;
    private final BakeryRepositorySupport bakeryRepositorySupport;

    @Transactional(readOnly = true)
    public List<BakeryCardDto> getBakeryList(Double latitude, Double longitude, Double height, Double width){
        return bakeryRepositorySupport.getBakeryList(latitude, longitude, height, width);
    }

    @Transactional(readOnly = true)
    public List<BakeryDto> getAllBakeryList(){
        return bakeryRepositorySupport.getAllBakeryList();
    }
}
