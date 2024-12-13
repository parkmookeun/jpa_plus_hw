package com.example.demo.repository;

import com.example.demo.entity.Reservation;

import java.util.List;

public interface QueryDslReservationRepository {
    List<Reservation> searchReservationsByQueryDsl(Long userId, Long itemId);
}
