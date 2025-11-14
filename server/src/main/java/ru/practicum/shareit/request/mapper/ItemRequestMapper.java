package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ItemRequestMapper {
    @Mapping(target = "items", ignore = true)
    ItemRequestDto toDto(ItemRequest entity);

    @Mapping(target = "ownerId", source = "owner.id")
    ItemRequestItemDto toItem(Item item);
}
