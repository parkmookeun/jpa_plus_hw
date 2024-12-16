package com.example.demo.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordEncoderTest {

    private static final String PASSWORD = "1234";

    @Test
    @DisplayName("패스워드 인코딩 테스트")
    void encodeAndMatchTest() {
        String encodedPassword = PasswordEncoder.encode(PASSWORD);

        boolean matches = PasswordEncoder.matches(PASSWORD, encodedPassword);

        assertThat(matches).isTrue();
    }

}