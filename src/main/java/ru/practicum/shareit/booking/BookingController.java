package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {


    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid BookingCreateDto bookingCreateDto) {
        return bookingService.createBooking(bookingCreateDto, userId);
    }

    @PatchMapping("{bookingId}")
    public BookingResponseDto changeBookingStatus(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long bookingId, @RequestParam(name = "approved") @NotNull boolean approved) {
        return bookingService.changeBookingStatus(userId, bookingId, approved);
    }

    @GetMapping("{bookingId}")
    public BookingResponseDto getBooking(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getCurrentUserBookings(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", required = false, defaultValue = "ALL") String state) {
        return bookingService.getCurrentUserBookings(userId, state);
    }
}
