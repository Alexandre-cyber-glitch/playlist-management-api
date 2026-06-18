package com.alexandreb.playlist.service.impl;

import com.alexandreb.playlist.dto.song.CreateSongRequest;
import com.alexandreb.playlist.dto.song.SongResponse;
import com.alexandreb.playlist.exception.ResourceNotFoundException;
import com.alexandreb.playlist.mapper.SongMapper;
import com.alexandreb.playlist.repository.SongRepository;
import com.alexandreb.playlist.service.SongService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {
    private final SongRepository songRepository;
    private final SongMapper songMapper;

    @Override
    public SongResponse create(CreateSongRequest request) {
        var songEntity = songMapper.toEntity(request);
        var savedSongEntity = songRepository.save(songEntity);
        return songMapper.toResponse(savedSongEntity);
    }

    @Override
    public SongResponse getById(Long id) {
        var songEntity = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Song not found with id " + id
                ));
        return songMapper.toResponse(songEntity);
    }

    @Override
    public List<SongResponse> findAll() {
        return songRepository.findAll()
                .stream()
                .map(songMapper::toResponse)
                .toList();
    }

    @Override
    public void delete(Long id) {
        if (!songRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Song not found with id " + id
            );
        }
        songRepository.deleteById(id);
    }
}
