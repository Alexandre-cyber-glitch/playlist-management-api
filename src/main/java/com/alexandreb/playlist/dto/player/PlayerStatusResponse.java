package com.alexandreb.playlist.dto.player;

import com.alexandreb.playlist.domain.PlayerStatus;
import com.alexandreb.playlist.dto.song.SongResponse;
import lombok.Builder;

@Builder
public record PlayerStatusResponse(
        PlayerStatus status,
        Long currentPlaylistId,
        SongResponse currentSong,
        Integer currentIndex
) {
}
