package com.alexandreb.playlist.service.impl;

import com.alexandreb.playlist.domain.Genre;
import com.alexandreb.playlist.dto.song.SongResponse;
import com.alexandreb.playlist.entity.PlaylistSongEntity;
import com.alexandreb.playlist.mapper.SongMapper;
import com.alexandreb.playlist.repository.PlaylistRepository;
import com.alexandreb.playlist.repository.PlaylistSongRepository;
import com.alexandreb.playlist.repository.SongRepository;
import com.alexandreb.playlist.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistSongRepository playlistSongRepository;
    private final SongRepository songRepository;
    private final SongMapper songMapper;

    @Override
    public List<SongResponse> recommend(Long playlistId) {

        var playlistSongs = playlistSongRepository.findByPlaylistIdOrderByPositionAsc(playlistId);

        if (playlistSongs.isEmpty()) {
            // var playlist = getPlaylistOrThrow(playlistRepository, playlistId);
            // var name = playlist.getName();
            // var desc = playlist.getDescription();
            // Future improvement:
            // recommendations could be generated from playlist metadata
            // such as name or description.
            return List.of();
        }

        var songIdsInPlaylist = playlistSongs.stream()
                .map(playlistSong -> playlistSong.getSong().getId())
                .collect(Collectors.toSet());


        // Count how many songs to each genre and determine the dominant
        var dominantGenre = getDominantGenre(playlistSongs);

        // playlist cannot be empty at this stage therefore a dominant genre will always exist

        // Recommend songs from the dominant genre while excluding songs already present in the playlist
        // Max 3, if not available,return []
        return songRepository.findByGenre(dominantGenre)
                .stream()
                .filter(song -> !songIdsInPlaylist.contains(song.getId()))
                .limit(3)
                .map(songMapper::toResponse)
                .toList();
    }

    private static Genre getDominantGenre(List<PlaylistSongEntity> playlistSongs) {
        Map<Genre, Integer> genreCounts = new HashMap<>();
        for (var playlistSong : playlistSongs) {
            var genre = playlistSong.getSong().getGenre();
            genreCounts.put(genre, genreCounts.getOrDefault(genre, 0) + 1);
        }

        // Find the most represented genre
        Genre dominantGenre = null;
        int maxCount = 0;
        for (var entry : genreCounts.entrySet()) {
            if (entry.getValue() > maxCount) {
                dominantGenre = entry.getKey();
                maxCount = entry.getValue();
            }
        }
        return dominantGenre;
    }
}
