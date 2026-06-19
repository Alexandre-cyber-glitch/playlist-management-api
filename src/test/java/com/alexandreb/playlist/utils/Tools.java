package com.alexandreb.playlist.utils;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class Tools {

    public static String createPlaylistAndReturnId(MockMvc mockMvc, String name, String description) throws Exception {
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

    public static String createSongAndReturnId(MockMvc mockMvc, String title, String artist, String genre, Integer durationInSeconds) throws Exception {
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

    public static String createPlaylistWithMusic(MockMvc mockMvc) throws Exception {
        var playlist = createPlaylistAndReturnId(mockMvc,"Weird", "test playlist");

        var song1 = createSongAndReturnId(mockMvc,"Numb", "Linkin Park", "ROCK", 185);
        var song2 = createSongAndReturnId(mockMvc,"In the end", "Linkin Park", "ROCK", 185);
        var song3 = createSongAndReturnId(mockMvc,"Burn it Down", "Linkin Park", "ROCK", 185);
        var song4 = createSongAndReturnId(mockMvc,"Blanck Space", "Taylor Swift", "POP", 185);
        var song5 =  createSongAndReturnId(mockMvc,"Cruel Summer", "Taylor Swift", "POP", 185);
        var song6 =  createSongAndReturnId(mockMvc,"Love Story", "Taylor Swift", "POP", 185);
        var song7 =  createSongAndReturnId(mockMvc,"Ode to Joy", "Ludwig van Beethoven", "CLASSICAL", 185);
        var song8 =  createSongAndReturnId(mockMvc,"Spring", "Antonio Vivaldi", "CLASSICAL", 185);
        var song9 =  createSongAndReturnId(mockMvc,"Fifth Symphony", "Ludwig van Beethoven", "CLASSICAL", 185);

        addSongToPlaylist(mockMvc, playlist, song1);
        addSongToPlaylist(mockMvc, playlist, song2);
        addSongToPlaylist(mockMvc, playlist, song3);
        addSongToPlaylist(mockMvc, playlist, song4);
        addSongToPlaylist(mockMvc, playlist, song5);
        addSongToPlaylist(mockMvc, playlist, song6);
        addSongToPlaylist(mockMvc, playlist, song7);
        addSongToPlaylist(mockMvc, playlist, song8);
        addSongToPlaylist(mockMvc, playlist, song9);

       return playlist;
    }

    public static void addSongToPlaylist(MockMvc mockMvc, String playlistId, String songId) throws Exception {
        mockMvc.perform(post("/api/playlists/{playlistId}/songs/{songId}", playlistId, songId))
                .andExpect(status().isOk());
    }
}
