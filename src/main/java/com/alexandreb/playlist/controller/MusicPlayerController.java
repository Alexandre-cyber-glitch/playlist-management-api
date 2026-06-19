package com.alexandreb.playlist.controller;

import com.alexandreb.playlist.dto.player.PlayerStatusResponse;
import com.alexandreb.playlist.service.MusicPlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/player")
@RequiredArgsConstructor
public class MusicPlayerController {

    private final MusicPlayerService musicPlayerService;

    @PostMapping("/play/playlist/{playlistId}")
    public PlayerStatusResponse playPlaylist(@PathVariable Long playlistId) {
        return musicPlayerService.playPlaylist(playlistId);
    }

    @PostMapping("/play/song/{songId}")
    public PlayerStatusResponse playSong(@PathVariable Long songId) {
        return musicPlayerService.playSong(songId);
    }

    @PostMapping("/pause")
    public PlayerStatusResponse pause() {
        return musicPlayerService.pause();
    }

    @PostMapping("/resume")
    public PlayerStatusResponse resume() {
        return musicPlayerService.resume();
    }

    @PostMapping("/stop")
    public PlayerStatusResponse stop() {
        return musicPlayerService.stop();
    }

    @PostMapping("/next")
    public PlayerStatusResponse next() {
        return musicPlayerService.next();
    }

    @PostMapping("/previous")
    public PlayerStatusResponse previous() {
        return musicPlayerService.previous();
    }

    @GetMapping("/status")
    public PlayerStatusResponse getStatus() {
        return musicPlayerService.getStatus();
    }
}
