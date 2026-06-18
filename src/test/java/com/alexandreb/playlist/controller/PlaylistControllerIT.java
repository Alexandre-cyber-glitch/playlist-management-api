package com.alexandreb.playlist.controller;

import com.alexandreb.playlist.repository.PlaylistRepository;
import com.alexandreb.playlist.repository.PlaylistSongRepository;
import com.alexandreb.playlist.repository.SongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

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
        var playlistId = createPlaylistAndReturnId("Workout", "Training playlist");
        var songId = createSongAndReturnId("Numb", "Linkin Park", "ROCK", 185);

        mockMvc.perform(post("/api/playlists/{playlistId}/songs/{songId}", playlistId, songId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(Integer.parseInt(playlistId)))
                .andExpect(jsonPath("$.name").value("Workout"));
    }

    @Test
    void shouldRemoveSongFromPlaylist() throws Exception {
        var playlistId = createPlaylistAndReturnId("Workout", "Training playlist");
        var songId = createSongAndReturnId("Numb", "Linkin Park", "ROCK", 185);

        mockMvc.perform(post("/api/playlists/{playlistId}/songs/{songId}", playlistId, songId))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/playlists/{playlistId}/songs/{songId}", playlistId, songId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(Integer.parseInt(playlistId)))
                .andExpect(jsonPath("$.name").value("Workout"));
    }

    private String createPlaylistAndReturnId(String name, String description) throws Exception {
        var response = mockMvc.perform(post("/api/playlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "%s",
                                  "description": "%s"
                                }
                                """.formatted(name, description)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return response.replaceAll(".*\"id\":(\\d+).*", "$1");
    }

    private String createSongAndReturnId(String title, String artist, String genre, Integer durationInSeconds) throws Exception {
        var response = mockMvc.perform(post("/api/songs").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "%s",
                                  "artist": "%s",
                                  "genre": "%s",
                                  "durationInSeconds": %d
                                }
                                """.formatted(title, artist, genre, durationInSeconds)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return response.replaceAll(".*\"id\":(\\d+).*", "$1");
    }
}
