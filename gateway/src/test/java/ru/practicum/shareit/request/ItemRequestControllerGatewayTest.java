package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerGatewayTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Test
    void addRequestDelegatesToClient() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("need drill");
        Map<String, Object> body = Map.of("id", 1, "description", "need drill");

        when(itemRequestClient.addRequest(ArgumentMatchers.eq(1L),
                ArgumentMatchers.any(ItemRequestCreateDto.class)))
                .thenReturn(ResponseEntity.ok(body));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("need drill"));

        verify(itemRequestClient).addRequest(ArgumentMatchers.eq(1L),
                ArgumentMatchers.any(ItemRequestCreateDto.class));
    }

    @Test
    void getOwnRequestsDelegatesToClient() throws Exception {
        List<Map<String, Object>> body = List.of(Map.of("id", 1));

        when(itemRequestClient.getOwnRequests(1L)).thenReturn(ResponseEntity.ok(body));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(itemRequestClient).getOwnRequests(1L);
    }

    @Test
    void getOtherUsersRequestsDelegatesToClient() throws Exception {
        List<Map<String, Object>> body = List.of(Map.of("id", 1));

        when(itemRequestClient.getOtherUsersRequests(1L)).thenReturn(ResponseEntity.ok(body));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(itemRequestClient).getOtherUsersRequests(1L);
    }

    @Test
    void getRequestByIdDelegatesToClient() throws Exception {
        Map<String, Object> body = Map.of("id", 1);

        when(itemRequestClient.getRequestById(1L, 1L)).thenReturn(ResponseEntity.ok(body));

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(itemRequestClient).getRequestById(1L, 1L);
    }

    @Test
    void addRequestWithBlankDescriptionReturnsBadRequest() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto("");

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemRequestClient);
    }
}
