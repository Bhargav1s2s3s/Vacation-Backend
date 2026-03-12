package com.vacation.service.impl;

import com.vacation.dto.LoginOrSignUpRequest;
import com.vacation.dto.LoginResponse;
import com.vacation.entity.UserEntity;
import com.vacation.error.exception.EmailAlreadyExistedEx;
import com.vacation.error.exception.UserAuthenticationException;
import com.vacation.error.exception.UsernameAlreadyExsitedEx;
import com.vacation.repository.ProfileRepository;
import com.vacation.repository.UserRepository;
import com.vacation.service.UserAuthenticationService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ProfileRepository profileRepository;

    @Transactional
    @Override
    public LoginResponse createUserSignUp(LoginOrSignUpRequest request) {

        if (request.getEmail() == null || request.getPassword() == null) {
            throw new UserAuthenticationException("Email or password required");
        }

        if (profileRepository.existsByEmailId(request.getEmail())) {
            throw new EmailAlreadyExistedEx("Email already registered");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyExsitedEx("username already registered");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(request.getPassword());

        UserEntity user = new UserEntity();
        user.setUsername(request.getEmail());
        user.setPassword(encodedPassword);

        user = userRepository.save(user);
        log.info("user entity has been saved: {}", user);

        profileRepository.setEmailIdInProfile(user.getId(), request.getEmail());
        log.info("email inserted into profile entity: {}", user);

        return LoginResponse.builder()
                .code(200)
                .build();
    }
}
