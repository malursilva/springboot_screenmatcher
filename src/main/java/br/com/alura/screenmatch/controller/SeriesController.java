package br.com.alura.screenmatch.controller;

import br.com.alura.screenmatch.dto.SeriesDTO;
import br.com.alura.screenmatch.repository.SeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SeriesController {
    @Autowired
    private SeriesRepository repository;

    @GetMapping("/series")
    public List<SeriesDTO> getSeries() {
        return repository.findAll()
                .stream()
                .map(s -> new SeriesDTO(s.getId(), s.getTitle(), s.getTotalSeasons(), s.getRating(),
                        s.getMainGenre(), s.getMainActors(), s.getSynopsis(), s.getPosterUrl()))
                .toList();
    }

}
