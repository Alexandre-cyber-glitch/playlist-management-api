package com.alexandreb.playlist.strategy.shuffle;

import com.alexandreb.playlist.entity.PlaylistSongEntity;

import java.util.List;

public class GenreBalancedShuffleStrategy implements ShuffleStrategy{
    @Override
    public List<PlaylistSongEntity> shuffle(List<PlaylistSongEntity> playlistSongEntities) {
        return List.of();
    }
}
