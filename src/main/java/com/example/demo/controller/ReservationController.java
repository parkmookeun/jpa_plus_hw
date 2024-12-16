package com.example.demo.controller;

import com.example.demo.dto.ReservationIdResponseDto;
import com.example.demo.dto.ReservationRequestDto;
import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.entity.Status;
import com.example.demo.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationIdResponseDto> createReservation(@RequestBody ReservationRequestDto reservationRequestDto) {

        ReservationIdResponseDto idResponseDto = reservationService.createReservation(reservationRequestDto.getItemId(),
                reservationRequestDto.getUserId(),
                reservationRequestDto.getStartAt(),
                reservationRequestDto.getEndAt());

        return new ResponseEntity<>(idResponseDto, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/update-status")
    public ResponseEntity<Void> updateReservation(@PathVariable Long id, @RequestBody Status status) {
        reservationService.updateReservationStatus(id, status);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponseDto>> findAll() {
        List<ReservationResponseDto> reservations =
                reservationService.getReservations();

        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ReservationResponseDto>> searchAll(@RequestParam(required = false) Long userId,
                          @RequestParam(required = false) Long itemId) {
        List<ReservationResponseDto> reservationResponseDtos
                = reservationService.searchAndConvertReservations(userId, itemId);

        return new ResponseEntity<>(reservationResponseDtos, HttpStatus.OK);
    }
}
