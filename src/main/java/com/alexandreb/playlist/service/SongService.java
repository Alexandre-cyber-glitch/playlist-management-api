package com.alexandreb.playlist.service;

import com.alexandreb.playlist.dto.song.CreateSongRequest;
import com.alexandreb.playlist.dto.song.SongResponse;

import java.util.List;

public interface SongService {
    SongResponse create(CreateSongRequest request);

    SongResponse getById(Long id);

    List<SongResponse> findAll();

    void delete(Long id);
}
