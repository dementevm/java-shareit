package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeption.EmailUniqueConstructException;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.exeption.UserAlreadyExistsException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public User findById(Long id) {
        if (!users.containsKey(id)) {
            throw new ObjectNotFoundException("User not found");
        }
        return users.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User save(User user) {
        String email = user.getEmail();
        if (email != null && users.values().stream()
                .map(User::getEmail)
                .filter(Objects::nonNull)
                .anyMatch(email::equals)) {
            throw new EmailUniqueConstructException("Пользователь с таким email уже существует");
        }

        String name = user.getName();
        if (name != null && users.values().stream()
                .map(User::getName)
                .filter(Objects::nonNull)
                .anyMatch(name::equals)) {
            throw new UserAlreadyExistsException("Пользователь с таким login уже существует");
        }
        user.setId(id);
        users.put(user.getId(), user);
        log.info("Создан пользователь с id - {}: {}", id, user);
        id += 1;
        return user;
    }

    @Override
    public User update(User user) {
        long userId = user.getId();
        User old = users.get(userId);
        if (old == null) {
            throw new ObjectNotFoundException(String.format("Пользователя с ID %d не существует", userId));
        }

        String email = user.getEmail();
        if (email != null) {
            boolean emailTaken = users.values().stream()
                    .filter(u -> !Objects.equals(u.getId(), userId))
                    .map(User::getEmail)
                    .filter(Objects::nonNull)
                    .anyMatch(email::equals);
            if (emailTaken) {
                throw new EmailUniqueConstructException("Email уже используется");
            }
        }

        users.put(userId, user);
        log.info("Пользователь с id - {} обновлен. Старые данные - {}. Новые данные - {}", userId, old, user);
        return user;
    }

    @Override
    public void delete(Long userId) {
        if (!users.containsKey(userId)) {
            throw new ObjectNotFoundException(String.format("Пользователя с ID %d не существует", userId));
        }
        users.remove(userId);
    }
}
