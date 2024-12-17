package com.example.demo.repository;

import com.example.demo.entity.Item;
import com.example.demo.entity.Users;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    void createItem(){

        Users users = new Users("user","abc@naver.com","moomin","1234");

        Item item = new Item("item1","desc1", users, users);

        userRepository.save(users);

        Item savedItem = itemRepository.saveAndFlush(item);
        Item findItem = itemRepository.findById(savedItem.getId()).orElseThrow();

        assertThat(findItem.getStatus()).isEqualTo("PENDING");
    }
}