package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    void saveAndFindAllReturnsPersistedUsers() {
        UserCreateDto dto = new UserCreateDto("user-name", "user@example.com");

        UserResponseDto saved = userService.save(dto);

        List<UserResponseDto> all = userService.findAll();

        assertThat(saved.id()).isGreaterThan(0L);
        assertThat(all).extracting(UserResponseDto::id).contains(saved.id());
        assertThat(all).extracting(UserResponseDto::email).contains("user@example.com");
    }
}
