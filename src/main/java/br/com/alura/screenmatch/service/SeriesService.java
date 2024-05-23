package br.com.alura.screenmatch.service;

import br.com.alura.screenmatch.dto.SeriesDTO;
import br.com.alura.screenmatch.repository.SeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeriesService {
    @Autowired
    private SeriesRepository repository;

    public List<SeriesDTO> getAllSeries() {
        return repository.findAll()
                .stream()
                .map(s -> new SeriesDTO(s.getId(), s.getTitle(), s.getTotalSeasons(), s.getRating(),
                        s.getMainGenre(), s.getMainActors(), s.getSynopsis(), s.getPosterUrl()))
                .toList();
    }
}
