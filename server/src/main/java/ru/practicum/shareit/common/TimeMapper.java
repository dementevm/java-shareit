package ru.practicum.shareit.common;

import org.mapstruct.Named;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TimeMapper {

    @Named("ldtToInstantUtc")
    public static Instant ldtToInstantUtc(LocalDateTime v) {
        return v == null ? null : v.toInstant(ZoneOffset.UTC);
    }

    @Named("instantToLdtUtc")
    public static LocalDateTime instantToLdtUtc(Instant v) {
        return v == null ? null : LocalDateTime.ofInstant(v, ZoneOffset.UTC);
    }
}