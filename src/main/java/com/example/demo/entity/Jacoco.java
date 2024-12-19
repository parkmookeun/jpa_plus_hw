package com.example.demo.entity;

public class Jacoco{
    public String select(String name) {
        switch (name) {
            case "딸기":
                return "빨간색입니다.";
            case "바나나":
                return "노란색입니다.";
            default:
                return "잘 모르겠습니다.";
        }
    }

    public String giveMeFruit() {
        return "과일주세요!";
    }
}