package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserResponseDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserResponseDto getUserById(@PathVariable("id") @Positive long id) {
        return userService.findById(id);
    }

    @PostMapping
    public UserResponseDto createUser(@Valid @RequestBody UserCreateDto dto) {
        return userService.save(dto);
    }

    @PatchMapping("/{id}")
    public UserResponseDto patchUser(@PathVariable @Positive long id,
                                     @RequestBody @Valid UserUpdateDto dto) {
        return userService.patch(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") @Positive long id) {
        userService.delete(id);
    }
}
