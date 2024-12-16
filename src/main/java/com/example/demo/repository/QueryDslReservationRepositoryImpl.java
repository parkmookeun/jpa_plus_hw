package com.example.demo.repository;

import com.example.demo.entity.QItem;
import com.example.demo.entity.QReservation;
import com.example.demo.entity.QUsers;
import com.example.demo.entity.Reservation;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QueryDslReservationRepositoryImpl implements QueryDslReservationRepository{

    private final JPAQueryFactory jpaQueryFactory;

    public QueryDslReservationRepositoryImpl(JPAQueryFactory jpaQueryFactory){
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<Reservation> searchReservationsByQueryDsl(Long userId, Long itemId) {
        QReservation reservation = QReservation.reservation;
        QItem item = QItem.item;
        QUsers user = QUsers.users;
        BooleanBuilder builder = new BooleanBuilder();

        // 조건 동적 추가
        if (userId != null) {
            builder.and(reservation.users.id.eq(userId));
        }
        if (itemId != null) {
            builder.and(reservation.item.id.eq(itemId));
        }

        return jpaQueryFactory
                .selectFrom(reservation)
                .leftJoin(reservation.item,item).fetchJoin()
                .leftJoin(reservation.users,user).fetchJoin()
                .where(builder)
                .fetch();
    }
}
