package com.alexandreb.playlist.mapper;

import com.alexandreb.playlist.dto.playlist.CreatePlaylistRequest;
import com.alexandreb.playlist.dto.playlist.PlaylistResponse;
import com.alexandreb.playlist.entity.PlaylistEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PlaylistMapper {

    PlaylistEntity toEntity(CreatePlaylistRequest request);

    PlaylistResponse toResponse(PlaylistEntity entity);
}
