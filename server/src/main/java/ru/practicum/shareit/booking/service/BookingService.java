package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemBookingException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UserForbiddenException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;

    @Transactional
    public BookingResponseDto createBooking(BookingCreateDto dto, long userId) {
        Booking booking = bookingMapper.toEntity(dto);
        User booker = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("User %d not found".formatted(userId)));
        Item item = itemRepository.findById(dto.itemId())
                .orElseThrow(() -> new ObjectNotFoundException("Item %d not found".formatted(dto.itemId())));
        booking.setBooker(booker);
        if (!item.isAvailable()) {
            throw new ItemBookingException("Item %d is not available".formatted(dto.itemId()));
        }
        booking.setItem(item);
        booking.setBookingStatus(Booking.BookingStatus.WAITING);
        Booking saved = bookingRepository.save(booking);
        return bookingMapper.toBookingDto(saved);
    }

    @Transactional
    public BookingResponseDto changeBookingStatus(long userId, long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ObjectNotFoundException("Booking %d not found".formatted(bookingId)));
        Item item = booking.getItem();
        User itemOwner = booking.getItem().getOwner();
        if (itemOwner.getId() != userId) {
            throw new UserForbiddenException("User %d doesn't own item %d".formatted(userId, item.getId()));
        }
        if (approved) {
            booking.setBookingStatus(Booking.BookingStatus.APPROVED);
        } else {
            booking.setBookingStatus(Booking.BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);
        return bookingMapper.toBookingDto(booking);
    }

    public BookingResponseDto getBooking(long userId, long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ObjectNotFoundException("Booking %d not found".formatted(bookingId)));
        if (userId == booking.getItem().getOwner().getId() || userId == booking.getBooker().getId()) {
            return bookingMapper.toBookingDto(booking);
        }
        throw new UserForbiddenException("User doesn't has access to Booking %d".formatted(bookingId));
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDto> getCurrentUserBookings(long userId, String stateRaw) {
        BookingState state = BookingState.from(stateRaw);
        Instant now = Instant.now();

        List<Booking> bookings = switch (state) {
            case ALL -> bookingRepository.findAllByBooker_IdOrderByStartDesc(userId);
            case CURRENT -> bookingRepository
                    .findAllByBooker_IdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(
                            userId, now, now);
            case PAST -> bookingRepository
                    .findAllByBooker_IdAndEndLessThanOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepository
                    .findAllByBooker_IdAndStartGreaterThanOrderByStartDesc(userId, now);
            case WAITING -> bookingRepository
                    .findAllByBooker_IdAndBookingStatusOrderByStartDesc(
                            userId, Booking.BookingStatus.WAITING);
            case REJECTED -> bookingRepository
                    .findAllByBooker_IdAndBookingStatusOrderByStartDesc(
                            userId, Booking.BookingStatus.REJECTED);
        };

        return bookings.stream().map(bookingMapper::toBookingDto).toList();
    }
}
