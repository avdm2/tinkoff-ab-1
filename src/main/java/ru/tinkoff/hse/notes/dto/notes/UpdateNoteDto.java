package ru.tinkoff.hse.notes.dto.notes;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Setter
@Getter
@Accessors(chain = true)
public class UpdateNoteDto {

    private String name;
    private String login;
    private String password;
    private String url;
    private UUID directoryUUID;
}
