package com.alexandreb.playlist.strategy.export;

import com.alexandreb.playlist.entity.PlaylistEntity;
import com.alexandreb.playlist.entity.PlaylistSongEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class M3uPlaylistExporter implements PlaylistExporter {

    /**
     * Export in M3U format
     * Ex :
     * #EXTM3U
     * <p>
     * #PLAYLIST:Workout
     * #DESCRIPTION:Training playlist
     * <p>
     * #EXTINF:185,Linkin Park - Numb
     * Numb.mp3
     * <p>
     * #EXTINF:185,Taylor Swift - Blank Space
     * Blank Space.mp3
     */
    @Override
    public String export(PlaylistEntity playlist, List<PlaylistSongEntity> playlistSongs) {
        var builder = new StringBuilder();

        builder.append("#EXTM3U\n\n");
        builder.append("#PLAYLIST:")
                .append(playlist.getName())
                .append("\n");

        if (playlist.getDescription() != null) {
            builder.append("#DESCRIPTION:")
                    .append(playlist.getDescription())
                    .append("\n");
        }
        builder.append("\n");
        for (var playlistSong : playlistSongs) {
            var song = playlistSong.getSong();
            builder.append("#EXTINF:")
                    .append(song.getDurationInSeconds())
                    .append(",")
                    .append(song.getArtist())
                    .append(" - ")
                    .append(song.getTitle())
                    .append("\n");

            builder.append(song.getTitle())
                    .append(".mp3")
                    .append("\n\n");
        }

        return builder.toString();
    }
}
