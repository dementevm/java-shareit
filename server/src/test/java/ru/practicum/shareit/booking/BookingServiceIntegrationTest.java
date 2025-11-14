package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void createBookingPersistsBookingAndReturnsDto() {
        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        User booker = new User();
        booker.setName("booker");
        booker.setEmail("booker@example.com");
        booker = userRepository.save(booker);

        Item item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(owner);
        item = itemRepository.save(item);

        Instant start = Instant.now().plus(1, ChronoUnit.DAYS);
        Instant end = Instant.now().plus(2, ChronoUnit.DAYS);
        BookingCreateDto dto = new BookingCreateDto(start, end, item.getId());

        BookingResponseDto result = bookingService.createBooking(dto, booker.getId());

        assertThat(result.id()).isGreaterThan(0L);
        assertThat(result.item().id()).isEqualTo(item.getId());
        assertThat(result.booker().id()).isEqualTo(booker.getId());
    }
}
