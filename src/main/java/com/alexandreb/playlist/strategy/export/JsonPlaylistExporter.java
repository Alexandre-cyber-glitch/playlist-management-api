package com.alexandreb.playlist.strategy.export;

import com.alexandreb.playlist.entity.PlaylistEntity;
import com.alexandreb.playlist.entity.PlaylistSongEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JsonPlaylistExporter implements PlaylistExporter {

    private final ObjectMapper objectMapper;

    /**
     * Export in JSON format
     * Ex :
     * {
     * "id": 1,
     * "name": "Workout",
     * "description": "Training playlist",
     * "songs": [
     * {
     * "title": "Numb",
     * "artist": "Linkin Park"
     * }
     * ]
     * }
     */
    @Override
    public String export(PlaylistEntity playlist, List<PlaylistSongEntity> playlistSongs) {
        try {
            var export = new PlaylistJsonExport(
                    playlist.getId(),
                    playlist.getName(),
                    playlist.getDescription(),
                    playlistSongs.stream()
                            .map(playlistSong -> {
                                var song = playlistSong.getSong();

                                return new SongJsonExport(
                                        song.getTitle(),
                                        song.getArtist(),
                                        song.getGenre().name(),
                                        song.getDurationInSeconds()
                                );
                            })
                            .toList()
            );
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(export);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Unable to export playlist as JSON", e);
        }
    }

    private record PlaylistJsonExport(
            Long id,
            String name,
            String description,
            List<SongJsonExport> songs
    ) {
    }

    private record SongJsonExport(
            String title,
            String artist,
            String genre,
            Integer durationInSeconds
    ) {
    }
}

