package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerGatewayTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    void createUserDelegatesToClientAndReturnsResponse() throws Exception {
        UserCreateDto dto = new UserCreateDto("user", "user@example.com");
        Map<String, Object> body = Map.of("id", 1, "name", "user", "email", "user@example.com");

        when(userClient.createUser(ArgumentMatchers.any(UserCreateDto.class)))
                .thenReturn(ResponseEntity.ok(body));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("user"))
                .andExpect(jsonPath("$.email").value("user@example.com"));

        verify(userClient).createUser(ArgumentMatchers.any(UserCreateDto.class));
    }

    @Test
    void getUserDelegatesToClient() throws Exception {
        Map<String, Object> body = Map.of("id", 1, "name", "user");
        when(userClient.getUser(1L)).thenReturn(ResponseEntity.ok(body));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(userClient).getUser(1L);
    }

    @Test
    void patchUserDelegatesToClient() throws Exception {
        UserUpdateDto dto = new UserUpdateDto(1L, "new", "new@example.com");
        Map<String, Object> body = Map.of("id", 1, "name", "new", "email", "new@example.com");

        when(userClient.patchUser(ArgumentMatchers.eq(1L), ArgumentMatchers.any(UserUpdateDto.class)))
                .thenReturn(ResponseEntity.ok(body));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("new"));

        verify(userClient).patchUser(ArgumentMatchers.eq(1L), ArgumentMatchers.any(UserUpdateDto.class));
    }

    @Test
    void deleteUserDelegatesToClient() throws Exception {
        when(userClient.deleteUser(1L)).thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(userClient).deleteUser(1L);
    }

    @Test
    void createUserWithInvalidEmailReturnsBadRequest() throws Exception {
        UserCreateDto dto = new UserCreateDto("user", "bad-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(userClient);
    }
}
