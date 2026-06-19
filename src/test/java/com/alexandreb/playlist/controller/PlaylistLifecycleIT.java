package com.alexandreb.playlist.controller;

import com.alexandreb.playlist.dto.song.SongResponse;
import com.alexandreb.playlist.repository.PlaylistRepository;
import com.alexandreb.playlist.repository.PlaylistSongRepository;
import com.alexandreb.playlist.repository.SongRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.alexandreb.playlist.utils.Tools.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PlaylistLifecycleIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private PlaylistSongRepository playlistSongRepository;

    @BeforeEach
    void setUp() throws Exception {
        playlistSongRepository.deleteAll();
        playlistRepository.deleteAll();
        songRepository.deleteAll();

        mockMvc.perform(post("/api/player/stop"))
                .andExpect(status().isOk());
    }

    /**
     * Life cycle :
     * create playlist
     * create songs
     * add songs
     * shuffle
     * recommend
     * add recommendations
     * play playlist
     * stop player
     * play single song
     */
    @Test
    @Transactional
    void shouldRunCompletePlaylistLifecycle() throws Exception {
        var playlistId = createPlaylistAndReturnId(mockMvc, "Lifecycle Mix", "Full API workflow");

        var song1 = createSongAndReturnId(mockMvc, "Blank Space", "Taylor Swift", "POP", 185);
        var song2 = createSongAndReturnId(mockMvc, "Cruel Summer", "Taylor Swift", "POP", 190);
        var song3 = createSongAndReturnId(mockMvc, "Numb", "Linkin Park", "ROCK", 185);

        createSongAndReturnId(mockMvc, "Love Story", "Taylor Swift", "POP", 200);
        createSongAndReturnId(mockMvc, "Style", "Taylor Swift", "POP", 210);

        addSongToPlaylist(mockMvc, playlistId, song1);
        addSongToPlaylist(mockMvc, playlistId, song2);
        addSongToPlaylist(mockMvc, playlistId, song3);

        mockMvc.perform(post("/api/playlists/{playlistId}/shuffle/{shuffleType}", playlistId, "SMART"))
                .andExpect(status().isOk());

        var recommendationResponse = mockMvc.perform(get("/api/playlists/{playlistId}/recommendations", playlistId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        var recommendations = objectMapper.readValue(
                recommendationResponse,
                new TypeReference<List<SongResponse>>() {
                }
        );

        assertThat(recommendations)
                .extracting(SongResponse::title)
                .containsExactlyInAnyOrder("Love Story", "Style");

        for (var recommendation : recommendations) {
            addSongToPlaylist(mockMvc, playlistId, String.valueOf(recommendation.id()));
        }

        mockMvc.perform(post("/api/player/play/playlist/{playlistId}", playlistId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PLAYING"))
                .andExpect(jsonPath("$.currentPlaylistId").value(Integer.parseInt(playlistId)))
                .andExpect(jsonPath("$.currentSong").exists());

        mockMvc.perform(post("/api/player/stop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("STOPPED"));

        mockMvc.perform(post("/api/player/play/song/{songId}", song3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PLAYING"))
                .andExpect(jsonPath("$.currentPlaylistId").doesNotExist())
                .andExpect(jsonPath("$.currentSong.title").value("Numb"));
    }
}
