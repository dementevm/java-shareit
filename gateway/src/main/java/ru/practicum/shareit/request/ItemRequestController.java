package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(
            @RequestHeader("X-Sharer-User-Id") @Positive long userId,
            @RequestBody @Valid ItemRequestCreateDto dto
    ) {
        return itemRequestClient.addRequest(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnRequests(
            @RequestHeader("X-Sharer-User-Id") @Positive long userId
    ) {
        return itemRequestClient.getOwnRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUsersRequests(
            @RequestHeader("X-Sharer-User-Id") @Positive long userId
    ) {
        return itemRequestClient.getOtherUsersRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(
            @RequestHeader("X-Sharer-User-Id") @Positive long userId,
            @PathVariable @Positive long requestId
    ) {
        return itemRequestClient.getRequestById(userId, requestId);
    }
}
