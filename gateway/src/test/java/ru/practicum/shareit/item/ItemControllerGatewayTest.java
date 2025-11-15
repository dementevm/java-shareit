package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerGatewayTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void createItemDelegatesToClient() throws Exception {
        ItemCreateDto dto = new ItemCreateDto("item", "description", true, null);
        Map<String, Object> body = Map.of("id", 1, "name", "item");

        when(itemClient.createItem(ArgumentMatchers.eq(1L), ArgumentMatchers.any(ItemCreateDto.class)))
                .thenReturn(ResponseEntity.ok(body));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("item"));

        verify(itemClient).createItem(ArgumentMatchers.eq(1L), ArgumentMatchers.any(ItemCreateDto.class));
    }

    @Test
    void getItemDelegatesToClient() throws Exception {
        Map<String, Object> body = Map.of("id", 1, "name", "item");

        when(itemClient.getItem(1L, 1L)).thenReturn(ResponseEntity.ok(body));

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(itemClient).getItem(1L, 1L);
    }

    @Test
    void getOwnerItemsDelegatesToClient() throws Exception {
        List<Map<String, Object>> body = List.of(Map.of("id", 1, "name", "item"));

        when(itemClient.getOwnerItems(1L)).thenReturn(ResponseEntity.ok(body));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(itemClient).getOwnerItems(1L);
    }

    @Test
    void searchItemsDelegatesToClient() throws Exception {
        List<Map<String, Object>> body = List.of(Map.of("id", 1, "name", "item"));

        when(itemClient.searchItems(1L, "text")).thenReturn(ResponseEntity.ok(body));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "text"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(itemClient).searchItems(1L, "text");
    }

    @Test
    void addCommentDelegatesToClient() throws Exception {
        CommentCreateDto dto = new CommentCreateDto("comment");
        Map<String, Object> body = Map.of("id", 1, "text", "comment");

        when(itemClient.addComment(ArgumentMatchers.eq(1L), ArgumentMatchers.eq(1L),
                ArgumentMatchers.any(CommentCreateDto.class)))
                .thenReturn(ResponseEntity.ok(body));

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("comment"));

        verify(itemClient).addComment(ArgumentMatchers.eq(1L), ArgumentMatchers.eq(1L),
                ArgumentMatchers.any(CommentCreateDto.class));
    }

    @Test
    void searchItemsWithBlankTextReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", " "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verifyNoInteractions(itemClient);
    }

    @Test
    void createItemWithNegativeUserIdReturnsBadRequest() throws Exception {
        ItemCreateDto dto = new ItemCreateDto("item", "description", true, null);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", -1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(itemClient);
    }
}
