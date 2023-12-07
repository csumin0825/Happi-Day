package com.happiday.Happi_Day.domain.service;

import com.happiday.Happi_Day.domain.entity.user.User;
import com.happiday.Happi_Day.domain.entity.user.dto.UserDeleteDto;
import com.happiday.Happi_Day.domain.entity.user.dto.UserResponseDto;
import com.happiday.Happi_Day.domain.entity.user.dto.UserUpdateDto;
import com.happiday.Happi_Day.domain.repository.UserRepository;
import com.happiday.Happi_Day.exception.CustomException;
import com.happiday.Happi_Day.exception.ErrorCode;
import com.happiday.Happi_Day.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService{

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserResponseDto getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserResponseDto.fromEntity(user);
    }

    @Transactional
    public UserResponseDto updateUserProfile(String username, UserUpdateDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        user.update(dto.toEntity(), passwordEncoder);
        userRepository.save(user);
        return UserResponseDto.fromEntity(user);
    }

    @Transactional
    public void deleteUser(String username, UserDeleteDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCHED);
        }
        userRepository.delete(user);
    }

}
