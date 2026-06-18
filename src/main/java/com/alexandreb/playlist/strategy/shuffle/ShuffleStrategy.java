package com.alexandreb.playlist.strategy.shuffle;

import com.alexandreb.playlist.entity.PlaylistSongEntity;

import java.util.List;

public interface ShuffleStrategy {
    List<PlaylistSongEntity> shuffle(List<PlaylistSongEntity> playlistSongEntities);
}
