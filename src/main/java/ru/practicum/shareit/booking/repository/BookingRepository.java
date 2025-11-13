package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker_IdOrderByStartDesc(long bookerId);

    List<Booking> findAllByBooker_IdAndStartLessThanEqualAndEndGreaterThanEqualOrderByStartDesc(
            long bookerId, Instant now1, Instant now2);

    List<Booking> findAllByBooker_IdAndEndLessThanOrderByStartDesc(
            long bookerId, Instant now);

    List<Booking> findAllByBooker_IdAndStartGreaterThanOrderByStartDesc(
            long bookerId, Instant now);

    List<Booking> findAllByBooker_IdAndBookingStatusOrderByStartDesc(
            long bookerId, Booking.BookingStatus status);

    boolean existsByBooker_IdAndItem_IdAndBookingStatusAndEndLessThanEqual(
            long bookerId, long itemId,
            Booking.BookingStatus status,
            java.time.Instant now
    );

    Optional<Booking> findFirstByItem_IdAndBookingStatusAndStartLessThanEqualOrderByStartDesc(
            long itemId, Booking.BookingStatus status, Instant now);

    Optional<Booking> findFirstByItem_IdAndBookingStatusAndStartAfterOrderByStartAsc(
            long itemId, Booking.BookingStatus status, Instant now);

}
