package com.alexandreb.playlist.repository;

import com.alexandreb.playlist.domain.Genre;
import com.alexandreb.playlist.entity.SongEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongRepository extends JpaRepository<SongEntity, Long> {
    List<SongEntity> findByGenre(Genre genre);
}
