package com.alexandreb.playlist.strategy.shuffle;

import com.alexandreb.playlist.entity.PlaylistSongEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomShuffleStrategy implements ShuffleStrategy {

    @Override
    public List<PlaylistSongEntity> shuffle(List<PlaylistSongEntity> playlistSongEntities) {
        var shuffled = new ArrayList<>(playlistSongEntities);
        Collections.shuffle(shuffled);
        return shuffled;
    }
}
