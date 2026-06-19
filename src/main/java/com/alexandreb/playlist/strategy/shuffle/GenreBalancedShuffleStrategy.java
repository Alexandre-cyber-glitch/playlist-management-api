package com.alexandreb.playlist.strategy.shuffle;

import com.alexandreb.playlist.entity.PlaylistSongEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenreBalancedShuffleStrategy implements ShuffleStrategy {

    /**
     * Shuffle strategy that attempts to distribute genres evenly throughout the playlist.
     *
     * Example:
     *
     * POP
     * POP
     * POP
     * ROCK
     * ROCK
     * JAZZ
     *
     * becomes:
     *
     * POP
     * ROCK
     * JAZZ
     * POP
     * ROCK
     * POP
     */
    @Override
    public List<PlaylistSongEntity> shuffle(List<PlaylistSongEntity> playlistSongEntities) {
        var remainingSongs = new ArrayList<>(playlistSongEntities);
        Collections.shuffle(remainingSongs); // Randomize the initial order to avoid preserving the playlist sequence

        // Group songs by genre
        var songsByGenre = remainingSongs.stream()
                .collect(Collectors.groupingBy(
                        playlistSong -> playlistSong.getSong().getGenre(),
                        LinkedHashMap::new,
                        Collectors.toCollection(ArrayList::new)
                ));

        var result = new ArrayList<PlaylistSongEntity>();

        // Pick one song from each genre at every iteration
        // until all songs have been consumed
        while (songsByGenre.values().stream().anyMatch(list -> !list.isEmpty())) {
            for (var songs : songsByGenre.values()) {
                if (!songs.isEmpty()) {
                    result.add(songs.remove(0));
                }
            }
        }
        return result;
    }
}
