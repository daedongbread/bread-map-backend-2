package com.depromeet.breadmapbackend.domain.admin.user;

import com.depromeet.breadmapbackend.domain.admin.user.dto.AdminUserDto;
import com.depromeet.breadmapbackend.domain.user.User;
import com.depromeet.breadmapbackend.domain.user.UserRepository;
import com.depromeet.breadmapbackend.global.dto.PageResponseDto;
import com.depromeet.breadmapbackend.global.exception.DaedongException;
import com.depromeet.breadmapbackend.global.exception.DaedongStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public PageResponseDto<AdminUserDto> getUserList(int page) {
        PageRequest pageable = PageRequest.of(page, 20, Sort.by("createdAt").descending());
        Page<User> all = userRepository.findPageAll(pageable);
        return PageResponseDto.of(all, AdminUserDto::new);
    }

    @Transactional(rollbackFor = Exception.class)
    public void changeUserBlock(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DaedongException(DaedongStatus.USER_NOT_FOUND));
        user.changeBlock();
    }
}
