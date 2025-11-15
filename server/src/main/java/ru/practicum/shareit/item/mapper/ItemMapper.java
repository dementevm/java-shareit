package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ItemMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "request", ignore = true)
    Item toEntity(ItemCreateDto dto);

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "name", source = "entity.name")
    @Mapping(target = "description", source = "entity.description")
    @Mapping(target = "available", source = "entity.available")
    @Mapping(target = "owner", expression = "java(toUserResponseDto(entity))")
    @Mapping(target = "request", expression = "java(toItemRequestResponseDto(entity))")
    @Mapping(target = "lastBooking", source = "lastBooking")
    @Mapping(target = "nextBooking", source = "nextBooking")
    @Mapping(target = "comments", source = "comments")
    ItemResponseDto toItemDto(
            Item entity,
            List<CommentResponseDto> comments,
            BookingShortDto lastBooking,
            BookingShortDto nextBooking
    );

    default ItemResponseDto toItemDto(Item entity, List<CommentResponseDto> comments) {
        return toItemDto(entity, comments, null, null);
    }

    @Mapping(target = "owner", expression = "java(toUserResponseDto(entity))")
    @Mapping(target = "request", expression = "java(toItemRequestResponseDto(entity))")
    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "comments", ignore = true)
    ItemResponseDto toItemDto(Item entity);

    List<ItemResponseDto> toItemDtoList(List<Item> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "request", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Item entity, ItemUpdateDto dto);

    default UserResponseDto toUserResponseDto(Item entity) {
        User u = entity.getOwner();
        return new UserResponseDto(u.getId(), u.getName(), u.getEmail());
    }

    default ItemRequestResponseDto toItemRequestResponseDto(Item entity) {
        ItemRequest ir = entity.getRequest();
        if (ir == null) {
            return null;
        }
        return new ItemRequestResponseDto(ir.getId(), ir.getDescription(), ir.getRequestor().getId());
    }
}