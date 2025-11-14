package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserResponseDto findById(Long id) {
        return userMapper.toUserDto(userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("User with id %d not found".formatted(id))));
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponseDto save(UserCreateDto dto) {
        User user = userMapper.toEntity(dto);
        User saved = userRepository.save(user);
        return userMapper.toUserDto(saved);
    }

    @Transactional
    public UserResponseDto update(Long id, UserUpdateDto dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("User with id %d not found".formatted(id)));
        userMapper.update(user, dto);
        User saved = userRepository.save(user);
        return userMapper.toUserDto(saved);
    }

    @Transactional
    public UserResponseDto patch(Long id, UserUpdateDto dto) {
        User user = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("User with id %d not found".formatted(id)));
        userMapper.update(user, dto);
        User saved = userRepository.save(user);
        return userMapper.toUserDto(saved);
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("User with id %d not found".formatted(id)));
        userRepository.delete(user);
    }
}
