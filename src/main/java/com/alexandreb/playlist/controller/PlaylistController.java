package com.alexandreb.playlist.controller;

import com.alexandreb.playlist.domain.ExportFormat;
import com.alexandreb.playlist.domain.ShuffleType;
import com.alexandreb.playlist.dto.playlist.CreatePlaylistRequest;
import com.alexandreb.playlist.dto.playlist.PlaylistResponse;
import com.alexandreb.playlist.dto.playlist.UpdatePlaylistRequest;
import com.alexandreb.playlist.dto.song.SongResponse;
import com.alexandreb.playlist.service.PlaylistService;
import com.alexandreb.playlist.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlists")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;
    private final RecommendationService recommendationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // 201 Created
    public PlaylistResponse create(@RequestBody CreatePlaylistRequest request) {
        return playlistService.create(request);
    }

    @GetMapping("/{id}")
    public PlaylistResponse getById(@PathVariable Long id) {
        return playlistService.getById(id);
    }

    @GetMapping
    public List<PlaylistResponse> findAll() {
        return playlistService.findAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 No Content
    public void delete(@PathVariable Long id) {
        playlistService.delete(id);
    }

    @PatchMapping("/{id}")
    public PlaylistResponse update(@PathVariable Long id, @RequestBody UpdatePlaylistRequest request
    ) {
        return playlistService.update(id, request);
    }

    @PostMapping("/{playlistId}/songs/{songId}")
    public PlaylistResponse addSong(@PathVariable Long playlistId, @PathVariable Long songId
    ) {
        return playlistService.addSong(playlistId, songId);
    }

    @DeleteMapping("/{playlistId}/songs/{songId}")
    public PlaylistResponse removeSong(@PathVariable Long playlistId, @PathVariable Long songId
    ) {
        return playlistService.removeSong(playlistId, songId);
    }

    @PostMapping("/{playlistId}/shuffle/{shuffleType}")
    public PlaylistResponse shuffle(@PathVariable Long playlistId, @PathVariable ShuffleType shuffleType
    ) {
        return playlistService.shuffle(playlistId, shuffleType);
    }

    @GetMapping(
            value = "/{playlistId}/export/{format}",
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public String export(@PathVariable Long playlistId, @PathVariable ExportFormat format
    ) {
        return playlistService.export(playlistId, format);
    }

    @GetMapping("/{playlistId}/recommendations")
    public List<SongResponse> recommend(@PathVariable Long playlistId) {
        return recommendationService.recommend(playlistId);
    }
}
