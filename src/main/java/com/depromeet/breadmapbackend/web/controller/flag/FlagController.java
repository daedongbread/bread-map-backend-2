package com.depromeet.breadmapbackend.web.controller.flag;

import com.depromeet.breadmapbackend.service.flag.FlagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Created by ParkSuHo by 2022/03/18.
 */
@Slf4j
@RestController
@RequestMapping("/flag")
@RequiredArgsConstructor
public class FlagController {

    private final FlagService flagService;

}
