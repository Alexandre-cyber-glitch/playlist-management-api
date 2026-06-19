package com.alexandreb.playlist.strategy.export;


import com.alexandreb.playlist.domain.ExportFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaylistExporterFactory {

    private final JsonPlaylistExporter jsonPlaylistExporter;
    private final M3uPlaylistExporter m3uPlaylistExporter;

    public PlaylistExporter getExporter(ExportFormat format) {
        return switch (format) {
            case JSON -> jsonPlaylistExporter;
            case M3U -> m3uPlaylistExporter;
        };
    }
}
