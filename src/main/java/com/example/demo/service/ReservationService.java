package com.example.demo.service;

import com.example.demo.dto.ReservationIdResponseDto;
import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.entity.*;
import com.example.demo.exception.ReservationConflictException;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.QueryDslReservationRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final QueryDslReservationRepository queryDslReservationRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RentalLogService rentalLogService;


    // TODO: 1. 트랜잭션 이해
    @Transactional
    public ReservationIdResponseDto createReservation(Long itemId, Long userId, LocalDateTime startAt, LocalDateTime endAt) {
        // 쉽게 데이터를 생성하려면 아래 유효성검사 주석 처리
        List<Reservation> haveReservations = reservationRepository.findConflictingReservations(itemId, startAt, endAt);
        if(!haveReservations.isEmpty()) {
            throw new ReservationConflictException("해당 물건은 이미 그 시간에 예약이 있습니다.");
        }

        Item item = itemRepository.findByIdOrElseThrow(itemId);
        Users users = userRepository.findByIdOrElseThrow(userId);
        Reservation reservation = new Reservation(item, users, Status.PENDING, startAt, endAt);
        Reservation savedReservation = reservationRepository.save(reservation);

        RentalLog rentalLog = new RentalLog(savedReservation, "로그 메세지", "CREATE");
        rentalLogService.save(rentalLog);

        return new ReservationIdResponseDto(savedReservation.getId());
    }

    // TODO: 3. N+1 문제
    public List<ReservationResponseDto> getReservations() {
//        List<Reservation> reservations = reservationRepository.findAll();
        List<Reservation> reservations = reservationRepository.findAllInfo();

        return reservations.stream().map(reservation -> {
            Users users = reservation.getUsers();
            Item item = reservation.getItem();

            return new ReservationResponseDto(
                    reservation.getId(),
                    users.getNickname(),
                    item.getName(),
                    reservation.getStartAt(),
                    reservation.getEndAt()
            );
        }).toList();
    }

    // TODO: 5. QueryDSL 검색 개선
    public List<ReservationResponseDto> searchAndConvertReservations(Long userId, Long itemId) {

        List<Reservation> reservations = searchReservations(userId, itemId);

        return convertToDto(reservations);
    }

    public List<Reservation> searchReservations(Long userId, Long itemId) {

//        if (userId != null && itemId != null) {
//            return reservationRepository.findByUserIdAndItemId(userId, itemId);
//        } else if (userId != null) {
//            return reservationRepository.findByUserId(userId);
//        } else if (itemId != null) {
//            return reservationRepository.findByItemId(itemId);
//        } else {
//            return reservationRepository.findAll();
//        }
        return queryDslReservationRepository.searchReservationsByQueryDsl(userId,itemId);
    }

    private List<ReservationResponseDto> convertToDto(List<Reservation> reservations) {
        return reservations.stream()
                .map(reservation -> new ReservationResponseDto(
                        reservation.getId(),
                        reservation.getUsers().getNickname(),
                        reservation.getItem().getName(),
                        reservation.getStartAt(),
                        reservation.getEndAt()
                ))
                .toList();
    }

    // TODO: 7. 리팩토링
    @Transactional
    public void updateReservationStatus(Long reservationId, Status status) {
        Reservation reservation = reservationRepository.findByIdOrElseThrow(reservationId);
//        if ("APPROVED".equals(status)) {
//            if (!"PENDING".equals(reservation.getStatus())) {
//                throw new IllegalArgumentException("PENDING 상태만 APPROVED로 변경 가능합니다.");
//            }
//            reservation.updateStatus("APPROVED");
//        } else if ("CANCELED".equals(status)) {
//            if ("EXPIRED".equals(reservation.getStatus())) {
//                throw new IllegalArgumentException("EXPIRED 상태인 예약은 취소할 수 없습니다.");
//            }
//            reservation.updateStatus("CANCELED");
//        } else if ("EXPIRED".equals(status)) {
//            if (!"PENDING".equals(reservation.getStatus())) {
//                throw new IllegalArgumentException("PENDING 상태만 EXPIRED로 변경 가능합니다.");
//            }
//            reservation.updateStatus("EXPIRED");
//        } else {
//            throw new IllegalArgumentException("올바르지 않은 상태: " + status);
//

//  위의 코드를 분석해보니 PENDING -> ALL / APPROVED -> CANCELED / CANCELED -> x / EXPIRED -> x
        if(Status.APPROVED.equals(reservation.getStatus())){
            if(!status.equals(Status.CANCELED)){
                throw new IllegalArgumentException("""
                        APPROVED -> %s 변경 불가능!
                        """.formatted(status.toString()));
            }
        }

        if(Status.CANCELED.equals(reservation.getStatus())){
            throw new IllegalArgumentException("""
                        CANCELED -> %s 변경 불가능!
                        """.formatted(status.toString()));
        }

        if(Status.EXPIRED.equals(reservation.getStatus())){
            throw new IllegalArgumentException("""
                        EXPIRED -> %s 변경 불가능!
                        """.formatted(status.toString()));
        }

        reservation.updateStatus(status);
    }
}
