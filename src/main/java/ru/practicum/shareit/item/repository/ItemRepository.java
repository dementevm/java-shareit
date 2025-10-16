package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item findById(long id);

    List<Item> findAllUsersItems(long useId);

    List<Item> findAllItems();

    Item save(Item item);

    Item update(Item item);

    void delete(long id);
}
