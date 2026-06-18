package com.alexandreb.playlist.service;

import com.alexandreb.playlist.domain.Genre;
import com.alexandreb.playlist.dto.CreateSongRequest;
import com.alexandreb.playlist.dto.SongResponse;
import com.alexandreb.playlist.entity.SongEntity;
import com.alexandreb.playlist.exception.ResourceNotFoundException;
import com.alexandreb.playlist.mapper.SongMapper;
import com.alexandreb.playlist.repository.SongRepository;
import com.alexandreb.playlist.service.impl.SongServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SongEntityServiceImplTest {

    @Mock
    private SongRepository songRepository;

    @Mock
    private SongMapper songMapper;

    @InjectMocks
    private SongServiceImpl songService;

    @Test
    void shouldCreateSong() {
        // given
        var request = new CreateSongRequest("Blank Space", "Taylor Swift", Genre.POP, 185);

        var songEntity = createEntityTest();
        var savedSongEntity = createEntityTest();
        var songResponse =  createSongResultTest();

        when(songMapper.toEntity(request)).thenReturn(songEntity);
        when(songRepository.save(songEntity)).thenReturn(savedSongEntity);
        when(songMapper.toResponse(savedSongEntity)).thenReturn(songResponse);

        // when
        SongResponse result = songService.create(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Blank Space");
        assertThat(result.artist()).isEqualTo("Taylor Swift");
        assertThat(result.genre()).isEqualTo(Genre.POP);

        verify(songMapper).toEntity(request);
        verify(songRepository).save(songEntity);
        verify(songMapper).toResponse(savedSongEntity);
    }

    @Test
    void shouldGetSongById() {
        // given
        Long songId = 1L;

        var songEntity = createEntityTest();
        var response =  createSongResultTest();

        when(songRepository.findById(songId)).thenReturn(Optional.of(songEntity));
        when(songMapper.toResponse(songEntity)).thenReturn(response);

        // when
        SongResponse result = songService.getById(songId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(songId);
        assertThat(result.title()).isEqualTo("Blank Space");

        verify(songRepository).findById(songId);
        verify(songMapper).toResponse(songEntity);
    }

    @Test
    void shouldFindAllSongs() {
        // given
        var songEntity = createEntityTest();

        var response =  createSongResultTest();

        when(songRepository.findAll()).thenReturn(List.of(songEntity));

        when(songMapper.toResponse(songEntity)).thenReturn(response);

        // when
        List<SongResponse> result = songService.findAll();

        // then
        assertThat(result)
                .hasSize(1)
                .extracting(SongResponse::title)
                .containsExactly("Blank Space");

        verify(songRepository).findAll();
    }

    @Test
    void shouldThrowWhenSongNotFound() {
        Long songId = 42L;
        when(songRepository.findById(songId)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> songService.getById(songId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Song not found with id 42");
        verify(songRepository).findById(songId);
    }

    private SongEntity createEntityTest() {
       return SongEntity.builder()
                .id(1L)
                .title("Blank Space")
                .artist("Taylor Swift")
                .genre(Genre.POP)
                .durationInSeconds(185)
                .build();
    }

    private SongResponse createSongResultTest() {
        return SongResponse.builder()
                .id(1L)
                .title("Blank Space")
                .artist("Taylor Swift")
                .genre(Genre.POP)
                .durationInSeconds(185)
                .build();
    }
}
