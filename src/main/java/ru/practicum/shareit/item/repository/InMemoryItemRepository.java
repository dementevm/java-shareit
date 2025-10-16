package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long id = 1;

    @Override
    public Item findById(long id) {
        if (!items.containsKey(id)) {
            throw new ObjectNotFoundException("Item with id " + id + " not found");
        }
        return items.get(id);
    }

    @Override
    public List<Item> findAllItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> findAllUsersItems(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == userId)
                .toList();
    }

    @Override
    public Item save(Item item) {
        item.setId(id);
        items.put(item.getId(), item);
        log.info("Создан предмет с id - {}. {}", id, item);
        id += 1;
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public void delete(long id) {
        if (!items.containsKey(id)) {
            throw new ObjectNotFoundException("Item with id " + id + " not found");
        }
        items.remove(id);
    }
}