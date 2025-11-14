package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerGatewayTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void bookItemDelegatesToClient() throws Exception {
        BookItemRequestDto dto = new BookItemRequestDto(1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));
        Map<String, Object> body = Map.of("id", 1);

        when(bookingClient.bookItem(ArgumentMatchers.eq(1L),
                ArgumentMatchers.any(BookItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok(body));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(bookingClient).bookItem(ArgumentMatchers.eq(1L),
                ArgumentMatchers.any(BookItemRequestDto.class));
    }

    @Test
    void getBookingDelegatesToClient() throws Exception {
        Map<String, Object> body = Map.of("id", 1);

        when(bookingClient.getBooking(1L, 1L)).thenReturn(ResponseEntity.ok(body));

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(bookingClient).getBooking(1L, 1L);
    }

    @Test
    void getCurrentUserBookingsDelegatesToClient() throws Exception {
        List<Map<String, Object>> body = List.of(Map.of("id", 1));

        when(bookingClient.getBookings(1L, BookingState.ALL, 0, 20))
                .thenReturn(ResponseEntity.ok(body));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(bookingClient).getBookings(1L, BookingState.ALL, 0, 20);
    }

    @Test
    void getOwnerBookingsDelegatesToClient() throws Exception {
        List<Map<String, Object>> body = List.of(Map.of("id", 1));

        when(bookingClient.getOwnerBookings(1L, BookingState.ALL, 0, 20))
                .thenReturn(ResponseEntity.ok(body));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));

        verify(bookingClient).getOwnerBookings(1L, BookingState.ALL, 0, 20);
    }

    @Test
    void approveDelegatesToClient() throws Exception {
        Map<String, Object> body = Map.of("id", 1, "approved", true);

        when(bookingClient.approve(1L, 1L, true)).thenReturn(ResponseEntity.ok(body));

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(bookingClient).approve(1L, 1L, true);
    }

    @Test
    void getCurrentUserBookingsWithUnknownStateReturnsServerError() throws Exception {
        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "UNKNOWN"))
                .andExpect(status().is5xxServerError());

        verifyNoInteractions(bookingClient);
    }
}
