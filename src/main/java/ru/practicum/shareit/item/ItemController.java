package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{itemId}")
    public ItemResponseDto getItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable("itemId") long itemId
    ) {
        return itemService.findById(userId, itemId);
    }

    @GetMapping
    public List<ItemResponseDto> getItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.findUserItems(userId);
    }

    @PostMapping
    public ItemResponseDto createItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid ItemCreateDto itemCreateDto) {
        return itemService.createItem(itemCreateDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto patchItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @RequestBody ItemUpdateDto itemUpdateDto,
                                     @PathVariable("itemId") @Positive long itemId) {
        return itemService.updateItem(itemUpdateDto, userId, itemId);

    }

    @GetMapping("/search")
    public List<ItemResponseDto> searchItems(@RequestParam(name = "text", required = true) String textForSearch) {
        if (textForSearch == null || textForSearch.isEmpty()) {
            return List.of();
        }
        return itemService.searchItems(textForSearch);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable long itemId,
            @RequestBody @Valid CommentCreateDto dto
    ) {
        return itemService.addComment(userId, itemId, dto);
    }
}

