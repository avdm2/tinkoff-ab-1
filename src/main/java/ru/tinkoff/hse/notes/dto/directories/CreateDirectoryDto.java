package ru.tinkoff.hse.notes.dto.directories;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Setter
@Getter
@Accessors(chain = true)
public class CreateDirectoryDto {

    private String name;
    private UUID parentDirectoryUUID;
}
