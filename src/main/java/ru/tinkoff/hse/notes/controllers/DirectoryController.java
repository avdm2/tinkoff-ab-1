package ru.tinkoff.hse.notes.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tinkoff.hse.notes.dto.directories.CreateDirectoryDto;
import ru.tinkoff.hse.notes.dto.directories.ResponseDirectoryDto;
import ru.tinkoff.hse.notes.entities.Directory;
import ru.tinkoff.hse.notes.mappers.DirectoryMapper;
import ru.tinkoff.hse.notes.services.DirectoryService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/directories")
public class DirectoryController {

    private final DirectoryService directoryService;

    public DirectoryController(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    @PostMapping("/create")
    public ResponseEntity<ResponseDirectoryDto> createDirectory(@RequestBody CreateDirectoryDto createDirectoryDto,
                                                                @AuthenticationPrincipal UserDetails userDetails) {
        String currentUser = userDetails.getUsername();

        Directory createdDirectory = directoryService.createDirectory(createDirectoryDto, currentUser);
        return ResponseEntity.ok().body(DirectoryMapper.toResponseDirectoryDto(createdDirectory));
    }

    @GetMapping("/list/{directoryUUID}")
    public ResponseEntity<List<ResponseDirectoryDto>> getDirectories(@PathVariable(required = false) UUID directoryUUID,
                                                                     @AuthenticationPrincipal UserDetails userDetails) {
        String currentUser = userDetails.getUsername();

        List<Directory> directories = directoryService.getAllSubdirectories(directoryUUID, currentUser);
        return ResponseEntity.ok().body(DirectoryMapper.toListResponseDirectoryDto(directories));
    }
}
