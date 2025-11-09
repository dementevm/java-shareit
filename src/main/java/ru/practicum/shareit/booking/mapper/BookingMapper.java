package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.common.TimeMapper;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, uses = TimeMapper.class)
public interface BookingMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "booker", ignore = true)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "bookingStatus", ignore = true)
    Booking toEntity(BookingCreateDto dto);

    @Mapping(target = "start", qualifiedByName = "instantToLdtUtc")
    @Mapping(target = "end", qualifiedByName = "instantToLdtUtc")
    @Mapping(target = "status", source = "bookingStatus")
    @Mapping(target = "booker", expression = "java(toUserResponseDto(entity))")
    @Mapping(target = "item", expression = "java(toItemShortDto(entity))")
    BookingResponseDto toBookingDto(Booking entity);

    default UserResponseDto toUserResponseDto(Booking entity) {
        User u = entity.getBooker();
        return new UserResponseDto(u.getId(), u.getName(), u.getEmail());
    }

    default ItemShortDto toItemShortDto(Booking entity) {
        Item i = entity.getItem();
        return new ItemShortDto(i.getId(), i.getName());
    }
}
