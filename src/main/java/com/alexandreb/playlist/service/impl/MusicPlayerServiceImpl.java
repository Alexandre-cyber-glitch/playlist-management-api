package com.alexandreb.playlist.service.impl;

import com.alexandreb.playlist.domain.PlayerStatus;
import com.alexandreb.playlist.dto.player.PlayerStatusResponse;
import com.alexandreb.playlist.entity.PlaylistSongEntity;
import com.alexandreb.playlist.exception.BusinessException;
import com.alexandreb.playlist.mapper.SongMapper;
import com.alexandreb.playlist.repository.PlaylistRepository;
import com.alexandreb.playlist.repository.PlaylistSongRepository;
import com.alexandreb.playlist.repository.SongRepository;
import com.alexandreb.playlist.service.MusicPlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.alexandreb.playlist.service.impl.ServiceTools.getPlaylistOrThrow;
import static com.alexandreb.playlist.service.impl.ServiceTools.getSongOrThrow;

/**
 * Global music player.
 * <p>
 * This service does not stream audio files.
 * It only manages playback state and navigation
 * across playlists and songs.
 */
@Service
@RequiredArgsConstructor
public class MusicPlayerServiceImpl implements MusicPlayerService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final SongRepository songRepository;
    private final SongMapper songMapper;

    private final Object lock = new Object();

    private PlayerStatus status = PlayerStatus.STOPPED;

    private Long currentPlaylistId;
    private List<PlaylistSongEntity> currentPlaylistSongs = new ArrayList<>();
    private int currentIndex = -1;

    @Override
    public PlayerStatusResponse playPlaylist(Long playlistId) {
        synchronized (lock) {
            getPlaylistOrThrow(playlistRepository, playlistId);

            var playlistSongs = playlistSongRepository.findByPlaylistIdOrderByPositionAsc(playlistId);
            if (playlistSongs.isEmpty()) {
                throw new BusinessException("Cannot play an empty playlist");
            }

            currentPlaylistId = playlistId;
            currentPlaylistSongs = new ArrayList<>(playlistSongs);
            currentIndex = 0;
            status = PlayerStatus.PLAYING;

            return buildStatusResponse();
        }
    }

    @Override
    public PlayerStatusResponse playSong(Long songId) {
        synchronized (lock) {
            var song = getSongOrThrow(songRepository, songId);

            currentPlaylistId = null;
            currentPlaylistSongs = List.of(
                    PlaylistSongEntity.builder()
                            .song(song)
                            .position(1)
                            .build());

            currentIndex = 0;
            status = PlayerStatus.PLAYING;

            return buildStatusResponse();
        }
    }

    @Override
    public PlayerStatusResponse pause() {
        synchronized (lock) {
            if (status == PlayerStatus.PLAYING) {
                status = PlayerStatus.PAUSED;
            }
            return buildStatusResponse();
        }
    }

    @Override
    public PlayerStatusResponse resume() {
        synchronized (lock) {
            if (status == PlayerStatus.PAUSED) {
                status = PlayerStatus.PLAYING;
            }
            return buildStatusResponse();
        }
    }

    @Override
    public PlayerStatusResponse stop() {
        synchronized (lock) {
            status = PlayerStatus.STOPPED;
            currentPlaylistId = null;
            currentPlaylistSongs = new ArrayList<>();
            currentIndex = -1;
            return buildStatusResponse();
        }
    }

    @Override
    public PlayerStatusResponse next() {
        synchronized (lock) {
            if (currentPlaylistSongs.isEmpty()) {
                return buildStatusResponse();
            }
            if (currentIndex < currentPlaylistSongs.size() - 1) {
                currentIndex++;
            } else {
                currentIndex = 0;
            }
            status = PlayerStatus.PLAYING;
            return buildStatusResponse();
        }
    }

    @Override
    public PlayerStatusResponse previous() {
        synchronized (lock) {
            if (currentPlaylistSongs.isEmpty()) {
                return buildStatusResponse();
            }
            if (currentIndex > 0) {
                currentIndex--;
            } else {
                currentIndex = currentPlaylistSongs.size() - 1;
            }
            status = PlayerStatus.PLAYING;
            return buildStatusResponse();
        }
    }

    @Override
    public PlayerStatusResponse getStatus() {
        synchronized (lock) {
            return buildStatusResponse();
        }
    }

    private PlayerStatusResponse buildStatusResponse() {
        var currentSong = currentIndex >= 0 && currentIndex < currentPlaylistSongs.size()
                ? songMapper.toResponse(currentPlaylistSongs.get(currentIndex).getSong())
                : null;

        return PlayerStatusResponse.builder()
                .status(status)
                .currentPlaylistId(currentPlaylistId)
                .currentSong(currentSong)
                .currentIndex(currentIndex)
                .build();
    }
}
