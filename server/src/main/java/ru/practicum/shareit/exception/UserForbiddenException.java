package ru.practicum.shareit.exception;

public class UserForbiddenException extends RuntimeException {
    public UserForbiddenException(String message) {
        super(message);
    }
}
