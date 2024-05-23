package br.com.alura.screenmatch.dto;

import br.com.alura.screenmatch.model.MediaGenre;

public record SeriesDTO(
        Long id,
        String title,
        Integer totalSeasons,
        Double rating,
        MediaGenre mainGenre,
        String mainActors,
        String synopsis,
        String posterUrl
) {
}
