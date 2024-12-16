package com.example.demo.dto;

import lombok.Getter;

@Getter
public class ReservationIdResponseDto {
    private Long id;

    public ReservationIdResponseDto(Long id) {
        this.id = id;
    }
}
