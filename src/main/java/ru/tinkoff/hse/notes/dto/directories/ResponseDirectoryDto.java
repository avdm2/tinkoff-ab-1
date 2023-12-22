package ru.tinkoff.hse.notes.dto.directories;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Setter
@Getter
@Accessors(chain = true)
public class ResponseDirectoryDto {

    private String name;
    private UUID parentDirectoryUUID;
    private String directoryOwner;
}
