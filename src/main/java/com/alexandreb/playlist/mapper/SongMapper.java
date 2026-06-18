package com.alexandreb.playlist.mapper;

import com.alexandreb.playlist.dto.CreateSongRequest;
import com.alexandreb.playlist.dto.SongResponse;
import com.alexandreb.playlist.entity.SongEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SongMapper {

    SongEntity toEntity(CreateSongRequest request);

    SongResponse toResponse(SongEntity songEntity);
}
