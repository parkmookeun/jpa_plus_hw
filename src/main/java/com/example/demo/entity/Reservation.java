package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;

    private LocalDateTime startAt;

    private LocalDateTime endAt;

    @Enumerated(EnumType.STRING)
    private Status status; // PENDING, APPROVED, CANCELED, EXPIRED

    public Reservation(Item item, Users users, Status status, LocalDateTime startAt, LocalDateTime endAt) {
        this.item = item;
        this.users = users;
        this.status = status;
        this.startAt = startAt;
        this.endAt = endAt;
    }

    public Reservation() {}

    public void updateStatus(Status status) {
        this.status = status;
    }
}
