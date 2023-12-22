package ru.tinkoff.hse.notes.services;

import org.springframework.stereotype.Service;
import ru.tinkoff.hse.notes.dto.directories.CreateDirectoryDto;
import ru.tinkoff.hse.notes.entities.Directory;
import ru.tinkoff.hse.notes.exceptions.AlreadyExistsException;
import ru.tinkoff.hse.notes.exceptions.NameNotFoundException;
import ru.tinkoff.hse.notes.exceptions.RequiredFieldsException;
import ru.tinkoff.hse.notes.exceptions.UUIDException;
import ru.tinkoff.hse.notes.repositories.DirectoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DirectoryService {

    private final DirectoryRepository directoryRepository;

    public DirectoryService(DirectoryRepository directoryRepository) {
        this.directoryRepository = directoryRepository;
    }

    public void createRoot(String username) {
        if (directoryRepository.findByNameAndDirectoryOwner("root", username).isEmpty()) {
            Directory root = new Directory()
                    .setName("root")
                    .setParentDirectory(null)
                    .setDirectoryOwner(username);
            directoryRepository.save(root);
        }
    }

    public Directory createDirectory(CreateDirectoryDto dto, String username)
            throws RequiredFieldsException, AlreadyExistsException, UUIDException {
        String directoryName = dto.getName();
        if (directoryName == null) {
            throw new RequiredFieldsException("Missing directory name.");
        }

        if (directoryRepository.findByNameAndDirectoryOwner(directoryName, username).isPresent()) {
            throw new AlreadyExistsException("Directory with such name is already exists.");
        }

        Directory directory = new Directory()
                .setName(directoryName)
                .setDirectoryOwner(username);

        UUID parentDirectoryUUID = dto.getParentDirectoryUUID();
        if (parentDirectoryUUID == null) {
            directory.setParentDirectory(directoryRepository.findByNameAndDirectoryOwner("root", username).get());
        } else {
            Directory parent = directoryRepository.findByIdAndDirectoryOwner(parentDirectoryUUID, username)
                    .orElseThrow(() -> new UUIDException("No directory with such UUID were found."));
            directory.setParentDirectory(parent);
        }

        directoryRepository.save(directory);
        return directory;
    }

    public List<Directory> getAllSubdirectories(UUID directoryUUID, String username) throws NameNotFoundException {
        Directory start;
        if (directoryUUID == null) {
            start = directoryRepository.findByNameAndDirectoryOwner("root", username).get();
        } else {
            start = directoryRepository.findByIdAndDirectoryOwner(directoryUUID, username)
                    .orElseThrow(() -> new NameNotFoundException("No directory with such name were found."));
        }

        List<Directory> allSubdirectories = new ArrayList<>();
        getSubDirectories(start, allSubdirectories, username);
        return allSubdirectories;
    }

    private void getSubDirectories(Directory directory, List<Directory> allSubdirectories, String username) {
        List<Directory> subdirectories = directoryRepository.findAllByParentDirectoryAndDirectoryOwner(directory, username);
        for (Directory subDirectory : subdirectories) {
            allSubdirectories.add(subDirectory);
            getSubDirectories(subDirectory, allSubdirectories, username);
        }
    }
}
