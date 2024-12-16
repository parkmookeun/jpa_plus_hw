package com.example.demo.service;

import com.example.demo.dto.ItemIdResponseDto;
import com.example.demo.entity.Item;
import com.example.demo.entity.Users;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ItemIdResponseDto createItem(String name, String description, Long ownerId, Long managerId) {
        Users owner = userRepository.findByIdOrElseThrow(ownerId);
        Users manager = userRepository.findByIdOrElseThrow(managerId);

        Item item = new Item(name, description, owner, manager);
        Item savedItem = itemRepository.save(item);

        return new ItemIdResponseDto(savedItem.getId());
    }
}
