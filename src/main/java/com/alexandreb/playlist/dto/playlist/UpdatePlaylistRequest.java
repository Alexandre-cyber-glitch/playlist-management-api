package com.alexandreb.playlist.dto.playlist;

import lombok.Builder;

@Builder
public record UpdatePlaylistRequest(
        String name,
        String description
) {
}
