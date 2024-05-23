package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.dto.EpisodeDTO;
import br.com.alura.screenmatch.dto.SeriesDTO;
import br.com.alura.screenmatch.model.Episode;
import br.com.alura.screenmatch.model.MediaGenre;
import br.com.alura.screenmatch.model.Series;
import br.com.alura.screenmatch.repository.SeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeriesService {
    @Autowired
    private SeriesRepository repository;

    public List<SeriesDTO> getAllSeries() {
        return convertListSeries(repository.findAll());
    }

    public List<SeriesDTO> getTop5Series() {
        return convertListSeries(repository.findTop5ByOrderByRatingDesc());
    }

    public List<SeriesDTO> getRecentReleases() {
        return convertListSeries(repository.searchSeriesWithRecentEpisodesReleased());
    }

    public SeriesDTO getSeriesById(Long id) {
        var series = repository.findById(id);
        return series.map(this::convertSeriesToDTO).orElse(null);
    }

    public List<SeriesDTO> getSeriesByGenre(String mainGenre) {
        var genre = MediaGenre.fromString(mainGenre);
        return convertListSeries(repository.findByMainGenre(genre));
    }

    public List<EpisodeDTO> getEpisodesFromAllSeasons(Long id) {
        Optional<Series> series = repository.findById(id);
        return series.map(value -> convertListEpisodes(value.getEpisodes())).orElse(null);
    }

    public List<EpisodeDTO> getEpisodesFromSeason(Long id, int number) {
        return convertListEpisodes(repository.episodesBySeriesAndSeason(id, number));
    }

    public List<EpisodeDTO> getTop5EpisodesFromSeries(Long id) {
        Optional<Series> series = repository.findById(id);
        return series.map(value -> convertListEpisodes(repository.searchTopEpisodesBySeries(value, 5))).orElse(null);
    }

    private List<SeriesDTO> convertListSeries(List<Series> seriesList) {
        return seriesList.stream().map(this::convertSeriesToDTO).toList();
    }

    private SeriesDTO convertSeriesToDTO(Series s) {
        return new SeriesDTO(s.getId(), s.getTitle(), s.getTotalSeasons(), s.getRating(),
                s.getMainGenre(), s.getMainActors(), s.getSynopsis(), s.getPosterUrl());
    }

    private List<EpisodeDTO> convertListEpisodes(List<Episode> episodeList) {
        return episodeList.stream().map(this::convertEpisodeToDTO).toList();
    }

    private EpisodeDTO convertEpisodeToDTO(Episode e) {
        return new EpisodeDTO(e.getSeasonNumber(), e.getNumber(), e.getTitle(), e.getRating());
    }
}
