package com.alexandreb.playlist.strategy.export;

import com.alexandreb.playlist.entity.PlaylistEntity;
import com.alexandreb.playlist.entity.PlaylistSongEntity;

import java.util.List;

public interface PlaylistExporter {
    String export(PlaylistEntity playlist, List<PlaylistSongEntity> playlistSongs);
}
