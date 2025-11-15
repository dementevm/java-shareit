package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;

    @Transactional
    public ItemRequestDto addRequest(long userId, ItemRequestCreateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("User with id %d not found".formatted(userId)));

        ItemRequest request = new ItemRequest();
        request.setDescription(dto.description());
        request.setRequestor(user);

        ItemRequest saved = itemRequestRepository.save(request);
        ItemRequestDto base = itemRequestMapper.toDto(saved);

        return new ItemRequestDto(base.id(), base.description(), base.created(), List.of());
    }

    public List<ItemRequestDto> getOwnRequests(long userId) {
        ensureUserExists(userId);

        List<ItemRequest> requests = itemRequestRepository.findAllByRequestor_IdOrderByCreatedDesc(userId);
        return mapRequestsWithItems(requests);
    }

    public List<ItemRequestDto> getOtherUsersRequests(long userId) {
        ensureUserExists(userId);

        List<ItemRequest> requests = itemRequestRepository.findAllByRequestor_IdNotOrderByCreatedDesc(userId);
        return mapRequestsWithItems(requests);
    }

    public ItemRequestDto getRequestById(long userId, long requestId) {
        ensureUserExists(userId);

        ItemRequest request = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException("Request with id %d not found".formatted(requestId)));

        List<Item> items = itemRepository.findAllByRequest_Id(requestId);
        List<ItemRequestItemDto> itemDtos = items.stream()
                .map(itemRequestMapper::toItem)
                .toList();

        ItemRequestDto base = itemRequestMapper.toDto(request);
        return new ItemRequestDto(base.id(), base.description(), base.created(), itemDtos);
    }

    private void ensureUserExists(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ObjectNotFoundException("User with id %d not found".formatted(userId));
        }
    }

    private List<ItemRequestDto> mapRequestsWithItems(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return List.of();
        }

        List<Long> ids = requests.stream()
                .map(ItemRequest::getId)
                .toList();

        List<Item> items = itemRepository.findAllByRequest_IdIn(ids);

        Map<Long, List<ItemRequestItemDto>> itemsByRequest = items.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getRequest().getId(),
                        Collectors.mapping(itemRequestMapper::toItem, Collectors.toList())
                ));

        return requests.stream()
                .map(r -> {
                    List<ItemRequestItemDto> itemDtos = itemsByRequest.getOrDefault(r.getId(), List.of());
                    ItemRequestDto base = itemRequestMapper.toDto(r);
                    return new ItemRequestDto(base.id(), base.description(), base.created(), itemDtos);
                })
                .toList();
    }
}
