package com.alexandreb.playlist.repository;

import com.alexandreb.playlist.entity.SongEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository
        extends JpaRepository<SongEntity, Long> {
}
