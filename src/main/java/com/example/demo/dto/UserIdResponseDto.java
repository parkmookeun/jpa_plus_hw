package com.example.demo.dto;

import lombok.Getter;

@Getter
public class UserIdResponseDto {
    private Long id;

    public UserIdResponseDto(Long id){
        this.id = id;
    }
}
