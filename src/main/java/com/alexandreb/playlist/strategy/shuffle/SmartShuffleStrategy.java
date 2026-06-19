package com.alexandreb.playlist.strategy.shuffle;

import com.alexandreb.playlist.entity.PlaylistSongEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

@Service
public class SmartShuffleStrategy implements ShuffleStrategy{

    /**
     * avoid as much as possible two identical artists in a row
     *
     * Example:
     *
     * Taylor Swift
     * Taylor Swift
     * Linkin Park
     *
     * becomes:
     *
     * Taylor Swift
     * Linkin Park
     * Taylor Swift
     */
    @Override
    public List<PlaylistSongEntity> shuffle(List<PlaylistSongEntity> playlistSongEntities) {
        var remainingSongs = new ArrayList<>(playlistSongEntities);
        Collections.shuffle(remainingSongs); // Randomize the initial order to avoid preserving the playlist sequence

        var result = new ArrayList<PlaylistSongEntity>();

        String lastArtist = null;

        while (!remainingSongs.isEmpty()) {
            String currentLastArtist = lastArtist;

            // Try to find a song from a different artist than the previous one
            // Greedy approach: tries to avoid consecutive repetitions when possible.
            // If the playlist distribution is unbalanced, consecutive artists may still occur because no valid alternative remains.
            var nextSong = remainingSongs.stream()
                    .filter(playlistSong ->
                            !playlistSong.getSong().getArtist().equals(currentLastArtist))
                    .findFirst()
                    .orElse(remainingSongs.get(0));

            result.add(nextSong);
            remainingSongs.remove(nextSong);
            lastArtist = nextSong.getSong().getArtist();
        }

        return result;
    }
}
