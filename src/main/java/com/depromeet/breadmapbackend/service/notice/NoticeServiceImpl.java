package com.depromeet.breadmapbackend.service.notice;

import com.depromeet.breadmapbackend.domain.notice.Notice;
import com.depromeet.breadmapbackend.domain.notice.NoticeType;
import com.depromeet.breadmapbackend.domain.notice.exception.NoticeDateException;
import com.depromeet.breadmapbackend.domain.notice.exception.NoticeTypeWrongException;
import com.depromeet.breadmapbackend.domain.notice.repository.NoticeRepository;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.exception.UserNotFoundException;
import com.depromeet.breadmapbackend.domain.user.repository.UserRepository;
import com.depromeet.breadmapbackend.web.controller.notice.dto.NoticeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService{
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    @Transactional
    public void addNotice(User user, NoticeType type, String name) {
        String content;
        if(type.equals(NoticeType.follow)) {
            content = name + "님이 회원님을 팔로우하기 시작했어요";
        }
        else if(type.equals(NoticeType.reviewLike)) {
            content = "'" + name + "'" + "리뷰를 " + "빵순이가 좋아해요!";
        }
        else if(type.equals(NoticeType.commentLike)) {
            content = "댓글을 " + name + "님이 좋아해요!";
        }
        else if(type.equals(NoticeType.comment)) {
            content = "'" + name + "'" + "리뷰에 댓글이 달렸어요";
        }
        else throw new NoticeTypeWrongException();

        Notice notice = Notice.builder().user(user).content(content).type(type).build();
        noticeRepository.save(notice);
    }

    @Transactional(readOnly = true)
    public List<NoticeDto> getNoticeList(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        return noticeRepository.findByUser(user).stream()
                .map(notice -> NoticeDto.builder()
                        .image("testImage").content(notice.getContent())
                        .createdAt(createAt(notice.getCreatedAt())).build())
                .collect(Collectors.toList());
    }

    private String createAt(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt.isAfter(now)) throw new NoticeDateException();
        long day = ChronoUnit.DAYS.between(createdAt, now);
        if (day >= 7) return createdAt.getMonthValue() + "월 " + createdAt.getDayOfMonth() + "일";
        else if (day > 0) return day + "일 전";

        long hour = ChronoUnit.HOURS.between(createdAt, now);
        if (hour > 0)  return hour + "시간 전";

        long minute = ChronoUnit.MINUTES.between(createdAt, now);
        return minute + "분전";
    }

}
