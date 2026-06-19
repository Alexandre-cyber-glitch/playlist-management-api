package com.alexandreb.playlist.service.impl;

import com.alexandreb.playlist.domain.ExportFormat;
import com.alexandreb.playlist.domain.ShuffleType;
import com.alexandreb.playlist.dto.playlist.CreatePlaylistRequest;
import com.alexandreb.playlist.dto.playlist.PlaylistResponse;
import com.alexandreb.playlist.dto.playlist.UpdatePlaylistRequest;
import com.alexandreb.playlist.entity.PlaylistSongEntity;
import com.alexandreb.playlist.exception.BusinessException;
import com.alexandreb.playlist.exception.ResourceNotFoundException;
import com.alexandreb.playlist.mapper.PlaylistMapper;
import com.alexandreb.playlist.repository.PlaylistRepository;
import com.alexandreb.playlist.repository.PlaylistSongRepository;
import com.alexandreb.playlist.repository.SongRepository;
import com.alexandreb.playlist.service.PlaylistService;
import com.alexandreb.playlist.strategy.export.PlaylistExporterFactory;
import com.alexandreb.playlist.strategy.shuffle.ShuffleStrategyFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.alexandreb.playlist.service.impl.ServiceTools.*;

@Service
@RequiredArgsConstructor
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final SongRepository songRepository;
    private final PlaylistMapper playlistMapper;
    private final ShuffleStrategyFactory shuffleStrategyFactory;
    private final PlaylistExporterFactory playlistExporterFactory;

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
    public PlaylistResponse update(Long id, UpdatePlaylistRequest request) {
        var playlist = getPlaylistOrThrow(playlistRepository, id);

        if (request.name() != null) {
            playlist.setName(request.name());
        }
        if (request.description() != null) {
            playlist.setDescription(request.description());
        }
        var playlistUpdated = playlistRepository.save(playlist);
        return playlistMapper.toResponse(playlistUpdated);
    }

    @Override
    public PlaylistResponse addSong(Long playlistId, Long songId) {
        var playlist = getPlaylistOrThrow(playlistRepository, playlistId);
        var song = getSongOrThrow(songRepository, songId);

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
        var playlist = getPlaylistOrThrow(playlistRepository, playlistId);
        var playlistSong = getPlaylistSongOrThrow(playlistSongRepository, playlistId, songId);

        playlistSongRepository.delete(playlistSong);
        reorderPlaylist(playlistSongRepository, playlistId);

        return playlistMapper.toResponse(playlist);
    }

    @Override
    public PlaylistResponse shuffle(Long playlistId, ShuffleType shuffleType) {
        var playlist = getPlaylistOrThrow(playlistRepository, playlistId);
        var playlistSongs = playlistSongRepository.findByPlaylistIdOrderByPositionAsc(playlistId);

        if (playlistSongs.size() <= 1) {
            // Nothing to shuffle : just one song in the playlist
            return playlistMapper.toResponse(playlist);
        }

        var shuffleStrategy = shuffleStrategyFactory.getStrategy(shuffleType);
        var shuffledSongs = shuffleStrategy.shuffle(playlistSongs);

        // reorder the new position in every PlaylistSong
        for (int i = 0; i < shuffledSongs.size(); i++) {
            shuffledSongs.get(i).setPosition(i + 1);
        }

        playlistSongRepository.saveAll(shuffledSongs);

        return playlistMapper.toResponse(playlist);
    }

    @Override
    public String export(Long playlistId, ExportFormat format) {
        var playlist = getPlaylistOrThrow(playlistRepository, playlistId);
        var playlistSongs = playlistSongRepository.findByPlaylistIdOrderByPositionAsc(playlistId);

        var exporter = playlistExporterFactory.getExporter(format);

        return exporter.export(playlist, playlistSongs);
    }
}
