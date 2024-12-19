package com.example.demo.service;

import com.example.demo.dto.ReservationIdResponseDto;
import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.entity.*;
import com.example.demo.exception.ReservationConflictException;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.QueryDslReservationRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private QueryDslReservationRepository queryDslReservationRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private RentalLogService rentalLogService;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    ReservationService reservationService;

    private Long ITEM_ID;
    private Long USER_ID;
    private Long RESERVATION_ID;
    private LocalDateTime START_AT;
    private LocalDateTime END_AT;
    private Item MOCK_ITEM;
    private Users MOCK_USERS;
    private Reservation MOCK_RESERVATION;
    @BeforeEach
    void setUp() {
        ITEM_ID = 1L;
        USER_ID = 1L;
        RESERVATION_ID = 1L;

        START_AT = LocalDateTime.parse("2024-12-17T20:18:00");
        END_AT = LocalDateTime.parse("2024-12-17T20:23:59");

        MOCK_ITEM = mock(Item.class);
        MOCK_USERS = mock(Users.class);
        MOCK_RESERVATION = mock(Reservation.class);
    }

    @Test
    void createReservation_Success() {
        // Given
        when(reservationRepository.findConflictingReservations(ITEM_ID, START_AT, END_AT))
                .thenReturn(Collections.emptyList());

        when(itemRepository.findByIdOrElseThrow(ITEM_ID))
                .thenReturn(MOCK_ITEM);

        when(userRepository.findByIdOrElseThrow(USER_ID))
                .thenReturn(MOCK_USERS);

        Reservation savedReservation = mock(Reservation.class);
        when(savedReservation.getId()).thenReturn(1L);
        when(reservationRepository.save(any(Reservation.class)))
                .thenReturn(savedReservation);

        // When
        ReservationIdResponseDto response = reservationService.createReservation(ITEM_ID, USER_ID, START_AT, END_AT);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());

        // Verify interactions
        verify(reservationRepository).findConflictingReservations(ITEM_ID, START_AT, END_AT);
        verify(itemRepository).findByIdOrElseThrow(ITEM_ID);
        verify(userRepository).findByIdOrElseThrow(ITEM_ID);
        verify(reservationRepository).save(any(Reservation.class));
        verify(rentalLogService).save(any(RentalLog.class));
    }

    @Test
    void createReservation_ConflictException() {
        // Given
        Reservation existingReservation = mock(Reservation.class);
        when(reservationRepository.findConflictingReservations(ITEM_ID, START_AT, END_AT))
                .thenReturn(List.of(existingReservation));

        // When & Then
        assertThrows(ReservationConflictException.class, () -> {
            reservationService.createReservation(ITEM_ID, USER_ID, START_AT, END_AT);
        });

        // Verify no further interactions
        verify(reservationRepository).findConflictingReservations(ITEM_ID, START_AT, END_AT);
        verifyNoMoreInteractions(itemRepository);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(reservationRepository);
        verifyNoMoreInteractions(rentalLogService);
    }

    @Test
    void createReservation_ItemNotFound() {
        // Given
        when(reservationRepository.findConflictingReservations(ITEM_ID, START_AT, END_AT))
                .thenReturn(Collections.emptyList());

        when(itemRepository.findByIdOrElseThrow(ITEM_ID))
                .thenThrow(new IllegalArgumentException("Item not found"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.createReservation(ITEM_ID, USER_ID, START_AT, END_AT);
        });
    }

    @Test
    void createReservation_UserNotFound() {
        // Given
        when(reservationRepository.findConflictingReservations(ITEM_ID, START_AT, END_AT))
                .thenReturn(Collections.emptyList());

        when(itemRepository.findByIdOrElseThrow(ITEM_ID))
                .thenReturn(MOCK_ITEM);

        when(userRepository.findByIdOrElseThrow(USER_ID))
                .thenThrow(new IllegalArgumentException("User not found"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.createReservation(ITEM_ID, USER_ID, START_AT, END_AT);
        });
    }

    @Test
    void getReservations_Success() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // 모의 사용자 생성
        Users mockUser1 = mock(Users.class);
        when(mockUser1.getNickname()).thenReturn("user1");

        Users mockUser2 = mock(Users.class);
        when(mockUser2.getNickname()).thenReturn("user2");

        // 모의 아이템 생성
        Item mockItem1 = mock(Item.class);
        when(mockItem1.getName()).thenReturn("item1");

        Item mockItem2 = mock(Item.class);
        when(mockItem2.getName()).thenReturn("item2");

        // 모의 예약 생성
        Reservation mockReservation1 = mock(Reservation.class);
        when(mockReservation1.getId()).thenReturn(1L);
        when(mockReservation1.getUsers()).thenReturn(mockUser1);
        when(mockReservation1.getItem()).thenReturn(mockItem1);
        when(mockReservation1.getStartAt()).thenReturn(now);
        when(mockReservation1.getEndAt()).thenReturn(now.plusDays(1));

        Reservation mockReservation2 = mock(Reservation.class);
        when(mockReservation2.getId()).thenReturn(2L);
        when(mockReservation2.getUsers()).thenReturn(mockUser2);
        when(mockReservation2.getItem()).thenReturn(mockItem2);
        when(mockReservation2.getStartAt()).thenReturn(now.plusDays(1));
        when(mockReservation2.getEndAt()).thenReturn(now.plusDays(2));

        // 리포지토리 모의 설정
        when(reservationRepository.findAllInfo())
                .thenReturn(Arrays.asList(mockReservation1, mockReservation2));

        // When
        List<ReservationResponseDto> result = reservationService.getReservations();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        // 첫 번째 예약 검증
        ReservationResponseDto dto1 = result.get(0);
        assertEquals(1L, dto1.getId());
        assertEquals("user1", dto1.getNickname());
        assertEquals("item1", dto1.getItemName());
        assertEquals(now, dto1.getStartAt());
        assertEquals(now.plusDays(1), dto1.getEndAt());

        // 두 번째 예약 검증
        ReservationResponseDto dto2 = result.get(1);
        assertEquals(2L, dto2.getId());
        assertEquals("user2", dto2.getNickname());
        assertEquals("item2", dto2.getItemName());
        assertEquals(now.plusDays(1), dto2.getStartAt());
        assertEquals(now.plusDays(2), dto2.getEndAt());

        // 리포지토리 메서드 호출 검증
        verify(reservationRepository).findAllInfo();
    }

    @Test
    void getReservations_EmptyList() {
        // Given
        when(reservationRepository.findAllInfo())
                .thenReturn(Arrays.asList());

        // When
        List<ReservationResponseDto> result = reservationService.getReservations();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // 리포지토리 메서드 호출 검증
        verify(reservationRepository).findAllInfo();
    }


    @Test
    void searchAndConvertReservationsTest() {
        // 모의 예약 생성
        Reservation mockReservation1 = mock(Reservation.class);
        when(mockReservation1.getId()).thenReturn(1L);
        when(mockReservation1.getUsers()).thenReturn(MOCK_USERS);
        when(mockReservation1.getItem()).thenReturn(MOCK_ITEM);

        when(queryDslReservationRepository.searchReservationsByQueryDsl(USER_ID,ITEM_ID))
                .thenReturn(List.of(mockReservation1));

        List<ReservationResponseDto> reservationResponseDtos =
                reservationService.searchAndConvertReservations(USER_ID, ITEM_ID);

        assertEquals(1L,reservationResponseDtos.get(0).getId());

    }

    @Test
    @DisplayName("PENDING 상태에서 모든 상태로 변경 가능")
    void updateFromPending() {
        // given
        Reservation reservation = new Reservation();

        reservation.updateStatus(Status.PENDING);
        given(reservationRepository.findByIdOrElseThrow(RESERVATION_ID)).willReturn(reservation);

        // when & then
        reservation.updateStatus(Status.PENDING);
        assertDoesNotThrow(() -> {
            reservationService.updateReservationStatus(RESERVATION_ID, Status.APPROVED);
            assertThat(reservation.getStatus()).isEqualTo(Status.APPROVED);
        });

        reservation.updateStatus(Status.PENDING);
        assertDoesNotThrow(() -> {
            reservationService.updateReservationStatus(RESERVATION_ID, Status.CANCELED);
            assertThat(reservation.getStatus()).isEqualTo(Status.CANCELED);
        });

        reservation.updateStatus(Status.PENDING);
        assertDoesNotThrow(() -> {
            reservationService.updateReservationStatus(RESERVATION_ID, Status.EXPIRED);
            assertThat(reservation.getStatus()).isEqualTo(Status.EXPIRED);
        });
    }

    @Test
    @DisplayName("CANCELED 상태에서 상태 변경 시 예외 발생")
    void updateFromCanceled(){
        //given
        Reservation reservation = new Reservation();
        reservation.updateStatus(Status.CANCELED);

        given(reservationRepository.findByIdOrElseThrow(RESERVATION_ID))
                .willReturn(reservation);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.updateReservationStatus(RESERVATION_ID,Status.PENDING);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.updateReservationStatus(RESERVATION_ID,Status.APPROVED);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.updateReservationStatus(RESERVATION_ID,Status.EXPIRED);
        });
    }

    @Test
    @DisplayName("EXPIRED 상태에서 상태 변경 시 예외 발생")
    void updateFromExpired(){
        //given
        Reservation reservation = new Reservation();
        reservation.updateStatus(Status.EXPIRED);

        given(reservationRepository.findByIdOrElseThrow(RESERVATION_ID))
                .willReturn(reservation);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.updateReservationStatus(RESERVATION_ID,Status.PENDING);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.updateReservationStatus(RESERVATION_ID,Status.APPROVED);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.updateReservationStatus(RESERVATION_ID,Status.CANCELED);
        });
    }

    @Test
    @DisplayName("APPROVED 상태에서 상태 변경 시 예외 발생")
    void updateFromApproved(){
        //given
        Reservation reservation = new Reservation();
        reservation.updateStatus(Status.APPROVED);

        given(reservationRepository.findByIdOrElseThrow(RESERVATION_ID))
                .willReturn(reservation);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.updateReservationStatus(RESERVATION_ID,Status.PENDING);
        });
        assertDoesNotThrow(() -> {
            reservationService.updateReservationStatus(RESERVATION_ID,Status.CANCELED);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            reservationService.updateReservationStatus(RESERVATION_ID,Status.EXPIRED);
        });
    }
}