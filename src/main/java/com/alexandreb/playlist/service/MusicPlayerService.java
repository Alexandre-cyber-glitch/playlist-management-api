package com.alexandreb.playlist.service;

import com.alexandreb.playlist.dto.player.PlayerStatusResponse;

public interface MusicPlayerService {

    PlayerStatusResponse playPlaylist(Long playlistId);

    PlayerStatusResponse playSong(Long songId);

    PlayerStatusResponse pause();

    PlayerStatusResponse resume();

    PlayerStatusResponse stop();

    PlayerStatusResponse next();

    PlayerStatusResponse previous();

    PlayerStatusResponse getStatus();
}
