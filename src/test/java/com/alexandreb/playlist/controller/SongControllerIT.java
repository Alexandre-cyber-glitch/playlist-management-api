package com.alexandreb.playlist.controller;

import com.alexandreb.playlist.repository.SongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * HTTP request
 * ↓
 * SongController
 * ↓
 * SongServiceImpl
 * ↓
 * SongRepository
 * ↓
 * H2 database
 * ↓
 * HTTP response
 */
@SpringBootTest
@AutoConfigureMockMvc
class SongControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SongRepository songRepository;

    @BeforeEach
    void setUp() {
        songRepository.deleteAll();
    }

    @Test
    void shouldCreateSong() throws Exception {
        mockMvc.perform(post("/api/songs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Blank Space",
                                  "artist": "Taylor Swift",
                                  "genre": "POP",
                                  "durationInSeconds": 185
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Blank Space"))
                .andExpect(jsonPath("$.artist").value("Taylor Swift"))
                .andExpect(jsonPath("$.genre").value("POP"))
                .andExpect(jsonPath("$.durationInSeconds").value(185));
    }

    @Test
    void shouldGetSongById() throws Exception {
        var response = mockMvc.perform(post("/api/songs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Numb",
                                  "artist": "Linkin Park",
                                  "genre": "ROCK",
                                  "durationInSeconds": 185
                                }
                                """))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var id = response.replaceAll(".*\"id\":(\\d+).*", "$1");

        mockMvc.perform(get("/api/songs/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Numb"))
                .andExpect(jsonPath("$.artist").value("Linkin Park"))
                .andExpect(jsonPath("$.genre").value("ROCK"));
    }

    @Test
    void shouldFindAllSongs() throws Exception {
        mockMvc.perform(post("/api/songs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Blank Space",
                                  "artist": "Taylor Swift",
                                  "genre": "POP",
                                  "durationInSeconds": 185
                                }
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/songs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Blank Space"));
    }

    @Test
    void shouldDeleteSong() throws Exception {
        var response = mockMvc.perform(post("/api/songs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Numb",
                                  "artist": "Linkin Park",
                                  "genre": "ROCK",
                                  "durationInSeconds": 185
                                }
                                """))
                .andReturn()
                .getResponse()
                .getContentAsString();

        var id = response.replaceAll(".*\"id\":(\\d+).*", "$1");

        mockMvc.perform(delete("/api/songs/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/songs/{id}", id))
                .andExpect(status().isNotFound());
    }
}