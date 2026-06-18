package com.alexandreb.playlist.repository;

import com.alexandreb.playlist.entity.PlaylistSongEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlaylistSongRepository extends JpaRepository<PlaylistSongEntity, Long> {

    List<PlaylistSongEntity> findByPlaylistIdOrderByPositionAsc(Long playlistId);

    Optional<PlaylistSongEntity> findByPlaylistIdAndSongId(Long playlistId, Long songId);

    boolean existsByPlaylistIdAndSongId(Long playlistId, Long songId);
}
