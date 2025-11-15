package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void addRequestReturnsRequestDto() throws Exception {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto("need drill");
        ItemRequestDto responseDto = new ItemRequestDto(1L, "need drill", Instant.now(), List.of());

        when(itemRequestService.addRequest(ArgumentMatchers.eq(1L), ArgumentMatchers.any(ItemRequestCreateDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("need drill"));
    }

    @Test
    void getOwnRequestsReturnsList() throws Exception {
        ItemRequestDto dto = new ItemRequestDto(1L, "need drill", Instant.now(), List.of());

        when(itemRequestService.getOwnRequests(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getOtherUsersRequestsReturnsList() throws Exception {
        ItemRequestItemDto item = new ItemRequestItemDto(10L, "drill", 2L);
        ItemRequestDto dto = new ItemRequestDto(1L, "need drill", Instant.now(), List.of(item));

        when(itemRequestService.getOtherUsersRequests(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].items[0].name").value("drill"));
    }

    @Test
    void getRequestByIdReturnsRequest() throws Exception {
        ItemRequestDto dto = new ItemRequestDto(1L, "need drill", Instant.now(), List.of());

        when(itemRequestService.getRequestById(1L, 1L)).thenReturn(dto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
}
