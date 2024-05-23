package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.dto.SeriesDTO;
import br.com.alura.screenmatch.model.Series;
import br.com.alura.screenmatch.repository.SeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    private List<SeriesDTO> convertListSeries(List<Series> seriesList) {
        return seriesList.stream().map(this::convertSeriesToDTO).toList();
    }

    private SeriesDTO convertSeriesToDTO(Series s) {
        return new SeriesDTO(s.getId(), s.getTitle(), s.getTotalSeasons(), s.getRating(),
                s.getMainGenre(), s.getMainActors(), s.getSynopsis(), s.getPosterUrl());
    }
}
