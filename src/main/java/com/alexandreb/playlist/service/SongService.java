package com.alexandreb.playlist.service;

import com.alexandreb.playlist.dto.CreateSongRequest;
import com.alexandreb.playlist.dto.SongResponse;

import java.util.List;

public interface SongService {
    SongResponse create(CreateSongRequest request);

    SongResponse getById(Long id);

    List<SongResponse> findAll();

    void delete(Long id);
}
