package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ItemServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void addCommentPersistsCommentForFinishedApprovedBooking() {
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

        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(Instant.now().minus(3, ChronoUnit.DAYS));
        booking.setEnd(Instant.now().minus(1, ChronoUnit.DAYS));
        booking.setBookingStatus(Booking.BookingStatus.APPROVED);
        bookingRepository.save(booking);

        CommentCreateDto dto = new CommentCreateDto("good item");

        CommentResponseDto result = itemService.addComment(booker.getId(), item.getId(), dto);

        assertThat(result.id()).isGreaterThan(0L);
        assertThat(result.text()).isEqualTo("good item");
        assertThat(result.authorName()).isEqualTo("booker");
        assertThat(commentRepository.findAll()).hasSize(1);
    }
}
