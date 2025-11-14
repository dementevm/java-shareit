package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UserForbiddenException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional(readOnly = true)
    public ItemResponseDto findById(long requesterId, long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Item with id %d not found".formatted(itemId)));

        List<Comment> comments = commentRepository.findAllByItem_IdOrderByCreatedDesc(item.getId());
        List<CommentResponseDto> commentDtos = commentMapper.toDtoList(comments);

        BookingShortDto lastDto = null;
        BookingShortDto nextDto = null;

        if (item.getOwner().getId() == requesterId) {
            Instant now = Instant.now();

            Optional<Booking> last = bookingRepository
                    .findFirstByItem_IdAndBookingStatusAndStartLessThanEqualOrderByStartDesc(
                            item.getId(), Booking.BookingStatus.APPROVED, now);

            Optional<Booking> next = bookingRepository
                    .findFirstByItem_IdAndBookingStatusAndStartAfterOrderByStartAsc(
                            item.getId(), Booking.BookingStatus.APPROVED, now);

            if (last.isPresent()) {
                Booking b = last.get();
                lastDto = new BookingShortDto(b.getId(), b.getBooker().getId());
            }
            if (next.isPresent()) {
                Booking b = next.get();
                nextDto = new BookingShortDto(b.getId(), b.getBooker().getId());
            }
        }

        return itemMapper.toItemDto(item, commentDtos, lastDto, nextDto);
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDto> findUserItems(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("User with id %d not found".formatted(id)));

        List<Item> items = itemRepository.findByOwner(user);
        if (items.isEmpty()) {
            return List.of();
        }

        List<Long> itemIds = items.stream().map(Item::getId).toList();
        List<Comment> comments = commentRepository.findAllByItem_IdInOrderByCreatedDesc(itemIds);

        Map<Long, List<Comment>> grouped = comments.stream()
                .collect(Collectors.groupingBy(c -> c.getItem().getId()));

        return items.stream()
                .map(it -> {
                    List<Comment> list = grouped.getOrDefault(it.getId(), List.of());
                    List<CommentResponseDto> dtoList = commentMapper.toDtoList(list);
                    return itemMapper.toItemDto(it, dtoList);
                })
                .toList();
    }

    @Transactional
    public ItemResponseDto createItem(ItemCreateDto createItemDto, long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id %d not found".formatted(userId)));

        Item item = itemMapper.toEntity(createItemDto);
        item.setOwner(user);

        Long requestId = createItemDto.requestId();
        if (requestId != null) {
            ItemRequest request = itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new ObjectNotFoundException("Request with id %d not found".formatted(requestId)));
            item.setRequest(request);
        }

        Item saved = itemRepository.save(item);
        return itemMapper.toItemDto(saved, List.of());
    }

    @Transactional
    public ItemResponseDto updateItem(ItemUpdateDto itemDto, long userId, long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Item with id %d not found".formatted(itemId)));
        if (item.getOwner().getId() != userId) {
            throw new UserForbiddenException(
                    "Item with id: %d doesn't belong to owner with id: %d".formatted(itemId, userId));
        }
        itemMapper.update(item, itemDto);
        Item updateItem = itemRepository.save(item);
        return itemMapper.toItemDto(updateItem, List.of());
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDto> searchItems(String searchText) {
        String q = searchText.toLowerCase();
        return itemRepository.findAll().stream()
                .filter(Item::isAvailable)
                .filter(it -> {
                    String name = it.getName() == null ? "" : it.getName().toLowerCase();
                    String desc = it.getDescription() == null ? "" : it.getDescription().toLowerCase();
                    return name.contains(q) || desc.contains(q);
                })
                .map(it -> itemMapper.toItemDto(it, List.of()))
                .toList();
    }

    @Transactional
    public CommentResponseDto addComment(long userId, long itemId, CommentCreateDto dto) {
        boolean allowed = bookingRepository.existsByBooker_IdAndItem_IdAndBookingStatusAndEndLessThanEqual(
                userId, itemId, Booking.BookingStatus.APPROVED, Instant.now());
        if (!allowed) {
            throw new BadRequestException("User has no completed bookings for this item");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "Item with id %d not found".formatted(itemId)));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(
                        "User with id %d not found".formatted(userId)));

        Comment comment = commentMapper.toEntity(dto);
        comment.setItem(item);
        comment.setAuthor(user);

        Comment saved = commentRepository.save(comment);
        return commentMapper.toDto(saved);
    }
}
