package com.alexandreb.playlist.dto.playlist;

import lombok.Builder;

@Builder
public record CreatePlaylistRequest(
        String name,
        String description
) {
}
