package ru.tinkoff.hse.notes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tinkoff.hse.notes.entities.Directory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DirectoryRepository extends JpaRepository<Directory, UUID> {

    Optional<Directory> findByNameAndDirectoryOwner(String name, String directoryOwner);
    Optional<Directory> findByIdAndDirectoryOwner(UUID directoryUUID, String directoryOwner);
    List<Directory> findAllByParentDirectoryAndDirectoryOwner(Directory parentDirectory, String directoryOwner);
}
