package com.depromeet.breadmapbackend.controller.member;

import com.depromeet.breadmapbackend.service.flag.FlagService;
import com.depromeet.breadmapbackend.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Created by ParkSuHo by 2022/03/18.
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
}
