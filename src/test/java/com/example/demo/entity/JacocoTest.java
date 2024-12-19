package com.example.demo.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JacocoTest {

    private Jacoco jacoco = new Jacoco();

    @Test
    public void 딸기_색깔을_잘_출력하는지_테스트() {
        String actual = jacoco.select("딸기");
        String expected = "빨간색입니다.";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void 바나나_색깔을_잘_출력하는지_테스트() {
        String actual = jacoco.select("바나나");
        String expected = "노란색입니다.";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void 그_외의_값을_잘_출력하는지_테스트() {
        String actual = jacoco.select("키위");
        String expected = "잘 모르겠습니다.";
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void giveMeFruit_Test() {
        String actual = "과일주세요!";
        String expected = jacoco.giveMeFruit();

        assertEquals(actual,expected);
    }
}