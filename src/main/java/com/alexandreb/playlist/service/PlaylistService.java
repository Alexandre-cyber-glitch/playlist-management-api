package com.alexandreb.playlist.service;

import com.alexandreb.playlist.domain.ExportFormat;
import com.alexandreb.playlist.domain.ShuffleType;
import com.alexandreb.playlist.dto.playlist.CreatePlaylistRequest;
import com.alexandreb.playlist.dto.playlist.PlaylistResponse;
import com.alexandreb.playlist.dto.playlist.UpdatePlaylistRequest;

import java.util.List;

public interface PlaylistService {
    PlaylistResponse create(CreatePlaylistRequest request);

    PlaylistResponse getById(Long id);

    List<PlaylistResponse> findAll();

    void delete(Long id);

    PlaylistResponse update(        Long id,UpdatePlaylistRequest request);

    PlaylistResponse addSong(Long playlistId, Long songId);

    PlaylistResponse removeSong(Long playlistId, Long songId);

    PlaylistResponse shuffle(Long playlistId, ShuffleType shuffleType);

    String export(Long playlistId, ExportFormat format);
}
