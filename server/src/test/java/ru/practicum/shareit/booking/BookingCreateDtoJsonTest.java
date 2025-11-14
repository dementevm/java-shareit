package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingCreateDto;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingCreateDtoJsonTest {

    @Autowired
    private JacksonTester<BookingCreateDto> json;

    @Test
    void serializeUsesConfiguredDateFormat() throws Exception {
        Instant start = Instant.parse("2025-06-01T10:15:30Z");
        Instant end = Instant.parse("2025-06-02T11:15:30Z");
        BookingCreateDto dto = new BookingCreateDto(start, end, 1L);

        JsonContent<BookingCreateDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2025-06-01T10:15:30");
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo("2025-06-02T11:15:30");
        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(1);
    }

    @Test
    void deserializeParsesConfiguredDateFormat() throws Exception {
        String content = "{"
                + "\"start\":\"2025-06-01T10:15:30\","
                + "\"end\":\"2025-06-02T11:15:30\","
                + "\"itemId\":5"
                + "}";

        BookingCreateDto dto = json.parseObject(content);

        assertThat(dto.itemId()).isEqualTo(5L);
        assertThat(dto.start()).isEqualTo(Instant.parse("2025-06-01T10:15:30Z"));
        assertThat(dto.end()).isEqualTo(Instant.parse("2025-06-02T11:15:30Z"));
    }
}
