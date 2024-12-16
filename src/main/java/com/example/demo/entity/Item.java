package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.DynamicInsert;


@Entity
@DynamicInsert
@Getter
// TODO: 6. Dynamic Insert
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Users owner;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Users manager;

    @Column(nullable = false, columnDefinition = "varchar(20) default 'PENDING'")
    private String status;

    public Item(String name, String description, Users manager, Users owner) {
        this.name = name;
        this.description = description;
        this.manager = manager;
        this.owner = owner;
    }

    public Item() {}
}
