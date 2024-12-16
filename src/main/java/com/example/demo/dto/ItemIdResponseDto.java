package com.example.demo.dto;

import lombok.Getter;

@Getter
public class ItemIdResponseDto {
    private Long id;

    public ItemIdResponseDto(Long id){
        this.id = id;
    }
}
