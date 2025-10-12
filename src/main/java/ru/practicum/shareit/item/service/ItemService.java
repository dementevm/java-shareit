package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.UserForbiddenException;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public ItemResponseDto findById(Long id) {
        return itemMapper.toItemDto(itemRepository.findById(id));
    }

    public List<ItemResponseDto> findUserItems(long id) {
        return itemMapper.toItemDtoList(itemRepository.findAllUsersItems(id));
    }

    public ItemResponseDto createItem(ItemCreateDto createItemDto, long userId) {
        User user = userRepository.findById(userId);
        Item item = itemMapper.toEntity(createItemDto);
        item.setOwner(user);
        return itemMapper.toItemDto(itemRepository.save(item));

    }

    public ItemResponseDto updateItem(ItemUpdateDto itemDto, long userId, long itemId) {
        Item item = itemRepository.findById(itemId);
        if (item.getOwner().getId() != userId) {
            throw new UserForbiddenException(String.format("Item with id: %d doesn't belong to owner with id: %d", itemId, userId));
        }
        itemMapper.update(item, itemDto);
        Item updateItem = itemRepository.update(item);
        return itemMapper.toItemDto(updateItem);
    }

    public List<ItemResponseDto> searchItems(String searchText) {
        String searchLower = searchText.toLowerCase();
        System.out.println(itemRepository.findAllItems());
        return itemRepository.findAllItems().stream()
                .filter(Item::isAvailable)
                .filter(item -> {
                    String name = item.getName() == null ? "" : item.getName().toLowerCase();
                    String desc = item.getDescription() == null ? "" : item.getDescription().toLowerCase();
                    return name.contains(searchLower) || desc.contains(searchLower);
                })
                .map(itemMapper::toItemDto)
                .toList();
    }
}
