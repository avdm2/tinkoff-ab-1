package ru.tinkoff.hse.notes.dto.notes;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Setter
@Getter
@Accessors(chain = true)
public class GetNotesDto {

    private UUID id;
    private String name;
}
