package com.alexandreb.playlist.strategy.shuffle;

import com.alexandreb.playlist.domain.ShuffleType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShuffleStrategyFactory {

    private final RandomShuffleStrategy randomShuffleStrategy;
    private final SmartShuffleStrategy smartShuffleStrategy;
    private final GenreBalancedShuffleStrategy genreBalancedShuffleStrategy;

    public ShuffleStrategy getStrategy(ShuffleType type) {
        return switch (type) {
            case RANDOM -> randomShuffleStrategy;
            case SMART -> smartShuffleStrategy;
            case GENRE_BALANCED -> genreBalancedShuffleStrategy;
        };
    }
}
