package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void getOtherUsersRequestsReturnsRequestsWithItems() {
        User requestor = new User();
        requestor.setName("requestor");
        requestor.setEmail("requestor@example.com");
        requestor = userRepository.save(requestor);

        User owner = new User();
        owner.setName("owner");
        owner.setEmail("owner@example.com");
        owner = userRepository.save(owner);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("need drill");
        itemRequest.setRequestor(requestor);
        itemRequest.setCreated(Instant.now());
        itemRequest = itemRequestRepository.save(itemRequest);

        Item item = new Item();
        item.setName("drill");
        item.setDescription("powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);
        item.setRequest(itemRequest);
        itemRepository.save(item);

        List<ItemRequestDto> result = itemRequestService.getOtherUsersRequests(owner.getId());

        assertThat(result).hasSize(1);
        ItemRequestDto dto = result.getFirst();
        assertThat(dto.description()).isEqualTo("need drill");
        assertThat(dto.items()).hasSize(1);
        assertThat(dto.items().getFirst().name()).isEqualTo("drill");
        assertThat(dto.items().getFirst().ownerId()).isEqualTo(owner.getId());
    }
}
