package ru.tinkoff.hse.notes.mappers;

import ru.tinkoff.hse.notes.dto.directories.ResponseDirectoryDto;
import ru.tinkoff.hse.notes.entities.Directory;

import java.util.List;

public class DirectoryMapper {

    public static ResponseDirectoryDto toResponseDirectoryDto(Directory directory) {
        return new ResponseDirectoryDto().setName(directory.getName()).setParentDirectoryUUID(directory.getParentDirectory().getId())
                .setDirectoryOwner(directory.getDirectoryOwner());
    }

    public static List<ResponseDirectoryDto> toListResponseDirectoryDto(List<Directory> directories) {
        return directories.stream().map(DirectoryMapper::toResponseDirectoryDto).toList();
    }
}
