package ru.tinkoff.hse.notes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tinkoff.hse.notes.entities.Note;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteRepository extends JpaRepository<Note, UUID> {

    List<Note> findAllByNoteOwner(String noteOwner);
    List<Note> findAllByNoteOwnerAndDirectoryUUID(String noteOwner, UUID directoryUUID);
    Optional<Note> getNoteByName(String name);
}
