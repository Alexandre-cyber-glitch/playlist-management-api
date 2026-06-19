package com.alexandreb.playlist.service;

import com.alexandreb.playlist.dto.song.SongResponse;

import java.util.List;

public interface RecommendationService {

    List<SongResponse> recommend(Long playlistId);
}
