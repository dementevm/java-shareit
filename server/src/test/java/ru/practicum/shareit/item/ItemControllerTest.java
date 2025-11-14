package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private ItemResponseDto sampleItemDto() {
        UserResponseDto owner = new UserResponseDto(1L, "owner", "owner@example.com");
        ItemRequestResponseDto request = null;
        BookingShortDto lastBooking = null;
        BookingShortDto nextBooking = null;
        return new ItemResponseDto(1L, "item", "description", true, owner, request, lastBooking, nextBooking, List.of());
    }

    @Test
    void createItemReturnsItem() throws Exception {
        ItemCreateDto createDto = new ItemCreateDto("item", "description", true, null);
        ItemResponseDto responseDto = sampleItemDto();

        when(itemService.createItem(ArgumentMatchers.any(ItemCreateDto.class), ArgumentMatchers.eq(1L)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("item"));
    }

    @Test
    void getItemReturnsItem() throws Exception {
        ItemResponseDto responseDto = sampleItemDto();

        when(itemService.findById(1L, 1L)).thenReturn(responseDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("item"));
    }

    @Test
    void getItemsReturnsOwnerItems() throws Exception {
        ItemResponseDto responseDto = sampleItemDto();

        when(itemService.findUserItems(1L)).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void patchItemUpdatesItem() throws Exception {
        ItemUpdateDto updateDto = new ItemUpdateDto("new-name", "new-description", true, 1L);
        ItemResponseDto responseDto = sampleItemDto();

        when(itemService.updateItem(ArgumentMatchers.any(ItemUpdateDto.class), ArgumentMatchers.eq(1L), ArgumentMatchers.eq(1L)))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void searchItemsReturnsItems() throws Exception {
        ItemResponseDto responseDto = sampleItemDto();

        when(itemService.searchItems("text")).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void addCommentReturnsComment() throws Exception {
        CommentCreateDto createDto = new CommentCreateDto("text");
        CommentResponseDto responseDto =
                new CommentResponseDto(1L, "text", "author", Instant.now());

        when(itemService.addComment(ArgumentMatchers.eq(1L), ArgumentMatchers.eq(1L),
                ArgumentMatchers.any(CommentCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("text"));
    }
}
