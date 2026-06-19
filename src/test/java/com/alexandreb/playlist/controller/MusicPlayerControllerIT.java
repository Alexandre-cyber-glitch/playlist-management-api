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

import static com.alexandreb.playlist.utils.Tools.addSongToPlaylist;
import static com.alexandreb.playlist.utils.Tools.createPlaylistAndReturnId;
import static com.alexandreb.playlist.utils.Tools.createSongAndReturnId;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class MusicPlayerControllerIT {

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
    void shouldPlaySingleSong() throws Exception {
        var songId = createSongAndReturnId(mockMvc, "Numb", "Linkin Park", "ROCK", 185);

        mockMvc.perform(post("/api/player/play/song/{songId}", songId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PLAYING"))
                .andExpect(jsonPath("$.currentPlaylistId").doesNotExist())
                .andExpect(jsonPath("$.currentSong.title").value("Numb"))
                .andExpect(jsonPath("$.currentSong.artist").value("Linkin Park"))
                .andExpect(jsonPath("$.currentIndex").value(0));
    }

    @Test
    @Transactional
    void shouldPlayPlaylistAndNavigate() throws Exception {
        var playlistId = createPlaylistAndReturnId(mockMvc, "Workout", "Training playlist");

        var song1 = createSongAndReturnId(mockMvc, "Numb", "Linkin Park", "ROCK", 185);
        var song2 = createSongAndReturnId(mockMvc, "Blank Space", "Taylor Swift", "POP", 185);

        addSongToPlaylist(mockMvc, playlistId, song1);
        addSongToPlaylist(mockMvc, playlistId, song2);

        mockMvc.perform(post("/api/player/play/playlist/{playlistId}", playlistId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PLAYING"))
                .andExpect(jsonPath("$.currentPlaylistId").value(Integer.parseInt(playlistId)))
                .andExpect(jsonPath("$.currentSong.title").value("Numb"))
                .andExpect(jsonPath("$.currentIndex").value(0));

        mockMvc.perform(post("/api/player/next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentSong.title").value("Blank Space"))
                .andExpect(jsonPath("$.currentIndex").value(1));

        mockMvc.perform(post("/api/player/previous"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentSong.title").value("Numb"))
                .andExpect(jsonPath("$.currentIndex").value(0));
    }

    @Test
    void shouldPauseResumeAndStopPlayer() throws Exception {
        var songId = createSongAndReturnId(mockMvc, "Numb", "Linkin Park", "ROCK", 185);

        mockMvc.perform(post("/api/player/play/song/{songId}", songId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PLAYING"));

        mockMvc.perform(post("/api/player/pause"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAUSED"));

        mockMvc.perform(post("/api/player/resume"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PLAYING"));

        mockMvc.perform(post("/api/player/stop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("STOPPED"))
                .andExpect(jsonPath("$.currentSong").doesNotExist())
                .andExpect(jsonPath("$.currentIndex").value(-1));
    }

    @Test
    void shouldReturnCurrentStatus() throws Exception {
        mockMvc.perform(get("/api/player/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("STOPPED"));
    }
}
