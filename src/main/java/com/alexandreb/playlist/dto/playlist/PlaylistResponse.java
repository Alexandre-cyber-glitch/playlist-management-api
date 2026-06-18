package com.alexandreb.playlist.dto.playlist;

import lombok.Builder;

@Builder
public record PlaylistResponse(
        Long id,
        String name,
        String description
) {
}
