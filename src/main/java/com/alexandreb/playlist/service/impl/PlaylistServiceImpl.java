package com.alexandreb.playlist.service.impl;

import com.alexandreb.playlist.dto.playlist.CreatePlaylistRequest;
import com.alexandreb.playlist.dto.playlist.PlaylistResponse;
import com.alexandreb.playlist.entity.PlaylistEntity;
import com.alexandreb.playlist.entity.PlaylistSongEntity;
import com.alexandreb.playlist.entity.SongEntity;
import com.alexandreb.playlist.exception.BusinessException;
import com.alexandreb.playlist.exception.ResourceNotFoundException;
import com.alexandreb.playlist.mapper.PlaylistMapper;
import com.alexandreb.playlist.repository.PlaylistRepository;
import com.alexandreb.playlist.repository.PlaylistSongRepository;
import com.alexandreb.playlist.repository.SongRepository;
import com.alexandreb.playlist.service.PlaylistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final SongRepository songRepository;
    private final PlaylistMapper playlistMapper;

    @Override
    public PlaylistResponse create(CreatePlaylistRequest request) {
        var playlistEntity = playlistMapper.toEntity(request);
        var savedPlaylistEntity = playlistRepository.save(playlistEntity);
        return playlistMapper.toResponse(savedPlaylistEntity);
    }

    @Override
    public PlaylistResponse getById(Long id) {
        var playlistEntity = playlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Playlist not found with id " + id
                ));

        return playlistMapper.toResponse(playlistEntity);
    }

    @Override
    public List<PlaylistResponse> findAll() {
        return playlistRepository.findAll()
                .stream()
                .map(playlistMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        if (!playlistRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Playlist not found with id " + id
            );
        }
        playlistRepository.deleteById(id);
    }

    @Override
    public PlaylistResponse addSong(Long playlistId, Long songId) {
        var playlist = getPlaylistOrThrow(playlistId);
        var song = getSongOrThrow(songId);

        var songAlreadyInPlaylist = playlistSongRepository.existsByPlaylistIdAndSongId(playlistId, songId);
        if (songAlreadyInPlaylist) {
            // already exist !
            throw new BusinessException("Song already exists in playlist");
        }

        // Then we create a PlaylistSong at the last position
        var nextPosition = playlistSongRepository.findByPlaylistIdOrderByPositionAsc(playlistId).size() + 1;
        var playlistSongEntity = PlaylistSongEntity.builder()
                .playlist(playlist)
                .song(song)
                .position(nextPosition)
                .build();

        playlistSongRepository.save(playlistSongEntity);

        return playlistMapper.toResponse(playlist);
    }

    @Override
    public PlaylistResponse removeSong(Long playlistId, Long songId) {
        var playlist = getPlaylistOrThrow(playlistId);
        var playlistSong = getPlaylistSongOrThrow(playlistId,  songId);

        playlistSongRepository.delete(playlistSong);
        reorderPlaylist(playlistId);

        return playlistMapper.toResponse(playlist);
    }

    private void reorderPlaylist(Long playlistId) {
        var remainingSongs = playlistSongRepository.findByPlaylistIdOrderByPositionAsc(playlistId);
        for (int i = 0; i < remainingSongs.size(); i++) {
            remainingSongs.get(i).setPosition(i + 1);
        }
        playlistSongRepository.saveAll(remainingSongs);
    }

    private PlaylistEntity getPlaylistOrThrow(Long playlistId) {
        return playlistRepository.findById(playlistId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Playlist not found with id " + playlistId
                ));
    }

    private SongEntity getSongOrThrow(Long songId) {
        return songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Song not found with id " + songId
                ));
    }

    private PlaylistSongEntity getPlaylistSongOrThrow(Long playlistId, Long songId) {
        return playlistSongRepository.findByPlaylistIdAndSongId(playlistId, songId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Song not found in playlist"
                ));
    }
}
