package br.com.alura.screenmatch.controller;

import br.com.alura.screenmatch.dto.EpisodeDTO;
import br.com.alura.screenmatch.dto.SeriesDTO;
import br.com.alura.screenmatch.service.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/series")
public class SeriesController {
    @Autowired
    private SeriesService service;

    @GetMapping
    public List<SeriesDTO> getSeries() {
        return service.getAllSeries();
    }

    @GetMapping("/top5")
    public List<SeriesDTO> getSeriesTop5() {
        return service.getTop5Series();
    }

    @GetMapping("/releases")
    public List<SeriesDTO> getRecentReleases() {
        return service.getRecentReleases();
    }

    @GetMapping("/{id}")
    public SeriesDTO getSeriesById(@PathVariable Long id) {
        return service.getSeriesById(id);
    }

    @GetMapping("{id}/season/all")
    public List<EpisodeDTO> getEpisodesFromAllSeasons(@PathVariable Long id) {
        return service.getEpisodesFromAllSeasons(id);
    }

    @GetMapping("{id}/season/{number}")
    public List<EpisodeDTO> getEpisodesFromSeason(@PathVariable Long id, @PathVariable int number) {
        return service.getEpisodesFromSeason(id, number);
    }
}
