package com.alexandreb.playlist.controller;

import com.alexandreb.playlist.dto.CreateSongRequest;
import com.alexandreb.playlist.dto.SongResponse;
import com.alexandreb.playlist.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/songs")
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // 201 Created
    public SongResponse create(@RequestBody CreateSongRequest request) {
        return songService.create(request);
    }

    @GetMapping("/{id}")
    public SongResponse getById(@PathVariable Long id) {
        return songService.getById(id);
    }

    @GetMapping
    public List<SongResponse> findAll() {
        return songService.findAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 No Content
    public void delete(@PathVariable Long id) {
        songService.delete(id);
    }
}
