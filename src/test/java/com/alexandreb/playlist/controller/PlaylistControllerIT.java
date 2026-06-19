package com.alexandreb.playlist.controller;

import com.alexandreb.playlist.repository.PlaylistRepository;
import com.alexandreb.playlist.repository.PlaylistSongRepository;
import com.alexandreb.playlist.repository.SongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

import static com.alexandreb.playlist.utils.Tools.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * MockMvc
 * ↓
 * Controller
 * ↓
 * Service
 * ↓
 * Repository
 * ↓
 * H2 database
 * ↓
 * Response HTTP
 */
@SpringBootTest
@AutoConfigureMockMvc
class PlaylistControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private PlaylistSongRepository playlistSongRepository;

    @BeforeEach
    void setUp() {
        playlistSongRepository.deleteAll();
        playlistRepository.deleteAll();
        songRepository.deleteAll();
    }

    @Test
    void shouldAddSongToPlaylist() throws Exception {
        var playlistId = createPlaylistAndReturnId(mockMvc, "Workout", "Training playlist");
        var songId = createSongAndReturnId(mockMvc, "Numb", "Linkin Park", "ROCK", 185);

        mockMvc.perform(post("/api/playlists/{playlistId}/songs/{songId}", playlistId, songId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(Integer.parseInt(playlistId)))
                .andExpect(jsonPath("$.name").value("Workout"));
    }

    @Test
    void shouldRemoveSongFromPlaylist() throws Exception {
        var playlistId = createPlaylistAndReturnId(mockMvc, "Workout", "Training playlist");
        var songId = createSongAndReturnId(mockMvc, "Numb", "Linkin Park", "ROCK", 185);

        mockMvc.perform(post("/api/playlists/{playlistId}/songs/{songId}", playlistId, songId))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/playlists/{playlistId}/songs/{songId}", playlistId, songId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(Integer.parseInt(playlistId)))
                .andExpect(jsonPath("$.name").value("Workout"));
    }

    @Test
    @Transactional
    void shouldShufflePlaylistWithRandomStrategy() throws Exception {
        var playlistId = createPlaylistWithMusic(mockMvc);

        var beforeShuffleTitles = playlistSongRepository
                .findByPlaylistIdOrderByPositionAsc(Long.valueOf(playlistId))
                .stream()
                .map(playlistSong -> playlistSong.getSong().getTitle())
                .toList();

        mockMvc.perform(post("/api/playlists/{playlistId}/shuffle/{shuffleType}", playlistId, "RANDOM")).andExpect(status().isOk());


        var afterShuffle = playlistSongRepository.findByPlaylistIdOrderByPositionAsc(Long.valueOf(playlistId));
        var afterShuffleTitles = afterShuffle.stream().map(playlistSong -> playlistSong.getSong().getTitle()).toList();

        assertThat(afterShuffleTitles).containsExactlyInAnyOrderElementsOf(beforeShuffleTitles);

        for (int i = 0; i < afterShuffle.size(); i++) {
            assertThat(afterShuffle.get(i).getPosition())
                    .as("Playlist songs should be reordered from 1 to n")
                    .isEqualTo(i + 1);
        }
    }

    @Test
    @Transactional
    void shouldShufflePlaylistWithSmartStrategy() throws Exception {
        var playlistId = createPlaylistWithMusic(mockMvc);

        var beforeShuffleTitles = playlistSongRepository
                .findByPlaylistIdOrderByPositionAsc(Long.valueOf(playlistId))
                .stream()
                .map(playlistSong -> playlistSong.getSong().getTitle())
                .toList();

        mockMvc.perform(post("/api/playlists/{playlistId}/shuffle/{shuffleType}", playlistId, "SMART")).andExpect(status().isOk());


        var afterShuffle = playlistSongRepository.findByPlaylistIdOrderByPositionAsc(Long.valueOf(playlistId));
        var afterShuffleTitles = afterShuffle.stream().map(playlistSong -> playlistSong.getSong().getTitle()).toList();

        assertThat(afterShuffleTitles).containsExactlyInAnyOrderElementsOf(beforeShuffleTitles);

        for (int i = 1; i < afterShuffle.size(); i++) {
            var previousArtist = afterShuffle.get(i - 1).getSong().getArtist();
            var currentArtist = afterShuffle.get(i).getSong().getArtist();
            assertThat(currentArtist).as("Two consecutive songs should not have the same artist")
                    .isNotEqualTo(previousArtist);
        }
    }

    @Test
    @Transactional
    void shouldShufflePlaylistWithGenreBalancedStrategy() throws Exception {
        var playlistId = createPlaylistWithMusic(mockMvc);

        var beforeShuffleTitles = playlistSongRepository
                .findByPlaylistIdOrderByPositionAsc(Long.valueOf(playlistId))
                .stream()
                .map(playlistSong -> playlistSong.getSong().getTitle())
                .toList();

        mockMvc.perform(post("/api/playlists/{playlistId}/shuffle/{shuffleType}", playlistId, "GENRE_BALANCED")).andExpect(status().isOk());


        var afterShuffle = playlistSongRepository.findByPlaylistIdOrderByPositionAsc(Long.valueOf(playlistId));
        var afterShuffleTitles = afterShuffle.stream().map(playlistSong -> playlistSong.getSong().getTitle()).toList();

        assertThat(afterShuffleTitles).containsExactlyInAnyOrderElementsOf(beforeShuffleTitles);

        for (int i = 1; i < afterShuffle.size(); i++) {
            var previousGenre = afterShuffle.get(i - 1).getSong().getGenre();
            var currentGenre = afterShuffle.get(i).getSong().getGenre();
            assertThat(currentGenre).as("Two consecutive songs should not have the same genre")
                    .isNotEqualTo(previousGenre);
        }
    }
}
