package ru.practicum.shareit.exeption;

public class EmailUniqueConstructException extends RuntimeException {
    public EmailUniqueConstructException(String message) {
        super(message);
    }
}
