package br.com.alura.screenmatch.repository;

import br.com.alura.screenmatch.model.MediaGenre;
import br.com.alura.screenmatch.model.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SeriesRepository extends JpaRepository<Series, Long> {
    Optional<Series> findByTitleContainingIgnoreCase(String nameSeries);
    List<Series> findByMainActorsContainingIgnoreCase(String name);
    List<Series> findTop5ByOrderByRatingDesc();
    List<Series> findByMainGenre(MediaGenre genre);

    @Query("select s from Series s WHERE s.totalSeasons <= :maxSeasons AND s.rating >= :minRating")
    List<Series> seriesBySeasonAmountAndRating(int maxSeasons, double minRating);
}
