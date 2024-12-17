package com.example.demo.controller;

import com.example.demo.dto.ReservationIdResponseDto;
import com.example.demo.dto.ReservationRequestDto;
import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.entity.Status;
import com.example.demo.interceptor.UserRoleInterceptor;
import com.example.demo.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@AutoConfigureDataJpa
@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @Mock
    MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private ReservationService mockReservationService;

    @Mock
    private UserRoleInterceptor userRoleInterceptor;

    @InjectMocks
    private ReservationController reservationController;

    @BeforeEach
    void setUp() throws Exception {
        // 인터셉터를 포함하여 MockMvc 설정
        mockMvc = MockMvcBuilders.standaloneSetup(reservationController)
                .addInterceptors(userRoleInterceptor) // 인터셉터 추가
                .build();

        // 인터셉터의 기본 동작 모킹 (필요한 경우)
        // 예: 모든 요청에 대해 인증을 통과하도록 설정
        when(userRoleInterceptor.preHandle(any(), any(), any())).thenReturn(true);
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @DisplayName("예약 생성 테스트")
    void createReservationTest() throws Exception {
        //given : request 알맞은 정보가 주어졌을 때,
        ReservationRequestDto requestDto = new ReservationRequestDto(1L, 1L,
                LocalDateTime.parse("2024-12-03T10:15:30"),
                LocalDateTime.parse("2024-12-25T12:30:00"));

        //when
        when(mockReservationService.createReservation(requestDto.getItemId(),
                requestDto.getUserId(),requestDto.getStartAt(),requestDto.getEndAt()))
                .thenReturn(new ReservationIdResponseDto(1L));

        //then
        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));

    }

    @Test
    @DisplayName("예약상태 변경 성공 테스트")
    void updateReservationTest_Success() throws Exception {
        //given : request 알맞은 정보가 주어졌을 때,
        Long reservationId = 1L;
        Status status = Status.APPROVED;

        //when

        //then
        mockMvc.perform(patch("/reservations/{id}/update-status",reservationId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(status)))
                .andExpect(status().isOk());

        //then
        verify(mockReservationService).updateReservationStatus(reservationId, status);
    }

    @Test
    @DisplayName("예약상태 변경 실패 테스트")
    void updateReservationTest_Fail() throws Exception {
        //given : request 알맞은 정보가 주어졌을 때,
        Long reservationId = 1L;

        //when and then
        mockMvc.perform(patch("/reservations/{id}/update-status",reservationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString("bad_status")))
                .andExpect(status().isBadRequest());


    }

    @Test
    @DisplayName("예약 모두 조회")
    void findAllTest() throws Exception {
        //given

        //when
        mockMvc.perform(get("/reservations")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        //then
        verify(mockReservationService).getReservations();
    }

    @Test
    @DisplayName("아이템과 유저로 예약 조회")
    void searchAllTest() throws Exception {
        //given
        Long USER_ID = 1L;
        Long ITEM_ID = 1L;
        MockHttpSession mockHttpSession = new MockHttpSession();

        List<ReservationResponseDto> responseList = Arrays.asList(
                new ReservationResponseDto(1L,"예약1","아이템1"
                        ,LocalDateTime.parse("2024-12-03T10:15:30")
                        ,LocalDateTime.parse("2024-12-03T10:15:30")
                        ),
                new ReservationResponseDto(2L,"예약2","아이템2"
                        ,LocalDateTime.parse("2024-12-03T10:15:30")
                        ,LocalDateTime.parse("2024-12-03T10:15:30")
                ));


        when(mockReservationService.searchAndConvertReservations(USER_ID,ITEM_ID))
                .thenReturn(responseList);

        //then
        mockMvc.perform(get("/reservations/search")
                .contentType(MediaType.APPLICATION_JSON)
                .session(mockHttpSession)
                .param("userId",String.valueOf(USER_ID))
                .param("itemId",String.valueOf(ITEM_ID)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].id").value(2L));
    }
}