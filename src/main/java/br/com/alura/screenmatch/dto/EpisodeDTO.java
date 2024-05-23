package br.com.alura.screenmatch.dto;

import java.time.LocalDate;

public record EpisodeDTO(
        Integer seasonNumber,
        Integer number,
        String title
) {
}
