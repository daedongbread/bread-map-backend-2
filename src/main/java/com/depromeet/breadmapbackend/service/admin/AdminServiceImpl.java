package com.depromeet.breadmapbackend.service.admin;

import com.depromeet.breadmapbackend.domain.bakery.Bakery;
import com.depromeet.breadmapbackend.domain.bakery.exception.BakeryNotFoundException;
import com.depromeet.breadmapbackend.domain.bakery.repository.BakeryRepository;
import com.depromeet.breadmapbackend.web.controller.admin.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{
    private final BakeryRepository bakeryRepository;

    @Transactional(readOnly = true)
    public List<AdminAllBakeryDto> getAllBakeryList() {
        return bakeryRepository.findAll()
                .stream().map(bakery -> {
                    return new AdminAllBakeryDto(bakery);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AdminBakeryDto getBakeryDetail(Long bakeryId) {
        Bakery bakery = bakeryRepository.findById(bakeryId).orElseThrow(BakeryNotFoundException::new);
        return new AdminBakeryDto(bakery);
    }
}
