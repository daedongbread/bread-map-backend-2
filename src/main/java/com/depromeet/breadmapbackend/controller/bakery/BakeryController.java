package com.depromeet.breadmapbackend.controller.bakery;

import com.depromeet.breadmapbackend.service.bakery.BakeryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Created by ParkSuHo by 2022/03/18.
 */
@Slf4j
@RestController
@RequestMapping("/bakery")
@RequiredArgsConstructor
public class BakeryController {
    private final BakeryService bakeryService;
}
