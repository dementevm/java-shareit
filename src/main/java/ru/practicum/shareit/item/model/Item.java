package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

@Data
public class Item {
    @NotNull
    long id;
    @NotBlank
    String name;
    @NotBlank
    String description;
    @NotNull
    boolean available;
    User owner;
    @NotNull
    String request;
}
