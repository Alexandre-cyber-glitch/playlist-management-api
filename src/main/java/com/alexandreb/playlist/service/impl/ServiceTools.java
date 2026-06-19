package com.alexandreb.playlist.service.impl;

import com.alexandreb.playlist.entity.PlaylistEntity;
import com.alexandreb.playlist.entity.PlaylistSongEntity;
import com.alexandreb.playlist.entity.SongEntity;
import com.alexandreb.playlist.exception.ResourceNotFoundException;
import com.alexandreb.playlist.repository.PlaylistRepository;
import com.alexandreb.playlist.repository.PlaylistSongRepository;
import com.alexandreb.playlist.repository.SongRepository;

public class ServiceTools {
    public static void reorderPlaylist(PlaylistSongRepository playlistSongRepository, Long playlistId) {
        var remainingSongs = playlistSongRepository.findByPlaylistIdOrderByPositionAsc(playlistId);
        for (int i = 0; i < remainingSongs.size(); i++) {
            remainingSongs.get(i).setPosition(i + 1);
        }
        playlistSongRepository.saveAll(remainingSongs);
    }

    public static PlaylistEntity getPlaylistOrThrow(PlaylistRepository playlistRepository, Long playlistId) {
        return playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Playlist not found with id " + playlistId
                ));
    }

    public static SongEntity getSongOrThrow(SongRepository songRepository, Long songId) {
        return songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Song not found with id " + songId
                ));
    }

    public static PlaylistSongEntity getPlaylistSongOrThrow(PlaylistSongRepository playlistSongRepository, Long playlistId, Long songId) {
        return playlistSongRepository.findByPlaylistIdAndSongId(playlistId, songId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Song not found in playlist"
                ));
    }
}
