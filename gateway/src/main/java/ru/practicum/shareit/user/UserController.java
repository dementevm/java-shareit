package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return userClient.getUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable @Positive long id) {
        return userClient.getUser(id);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid UserCreateDto dto) {
        return userClient.createUser(dto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchUser(
            @PathVariable @Positive long id,
            @RequestBody @Valid UserUpdateDto dto
    ) {
        return userClient.patchUser(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable @Positive long id) {
        return userClient.deleteUser(id);
    }
}
