package com.example.demo.repository;

import com.example.demo.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByEmail(String email);

    @Modifying
    @Query("UPDATE Users u SET u.status = 'BLOCKED' WHERE u.id IN :userIds")
    void updateStatusToBlockedAll(List<Long> userIds);

    default Users findByIdOrElseThrow(Long id){
        return findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 ID에 맞는 값이 존재하지 않습니다.")
        );
    }
}
