package ru.practicum.shareit.booking.model;

public enum BookingState {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static BookingState from(String raw) {
        if (raw == null || raw.isBlank()) return ALL;
        try {
            return BookingState.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown state: " + raw);
        }
    }
}
