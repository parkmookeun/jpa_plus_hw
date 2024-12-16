package com.example.demo.service;

import com.example.demo.dto.Authentication;
import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.UserIdResponseDto;
import com.example.demo.dto.UserRequestDto;
import com.example.demo.entity.Users;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.PasswordEncoder;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public UserIdResponseDto signupWithEmail(UserRequestDto userRequestDto) {
        String encodedPassword = PasswordEncoder.encode(userRequestDto.getPassword());
        userRequestDto.updatePassword(encodedPassword);

        Users savedUsers = userRepository.save(userRequestDto.toEntity());

        return new UserIdResponseDto(savedUsers.getId());
    }

    public Authentication loginUser(LoginRequestDto loginRequestDto) {
        Users users = userRepository.findByEmail(loginRequestDto.getEmail());
        if (users == null || !PasswordEncoder.matches(loginRequestDto.getPassword(), users.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 사용자 이름 혹은 잘못된 비밀번호");
        }
        return new Authentication(users.getId(), users.getRole());
    }
}
