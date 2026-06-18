package com.alexandreb.playlist.dto.song;

import com.alexandreb.playlist.domain.Genre;
import lombok.Builder;

@Builder
public record SongResponse(
        Long id,
        String title,
        String artist,
        Genre genre,
        Integer durationInSeconds
) {
}
