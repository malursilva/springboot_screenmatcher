package br.com.alura.screenmatch.model;

import java.util.OptionalDouble;

public class Series {
    private String title;
    private Integer totalSeasons;
    private Double rating;
    private MediaGenre mainGenre;
    private String mainActors;
    private String synopsis;
    private String posterUrl;

    public Series(SeriesData seriesData) {
        this.title = seriesData.title();
        this.totalSeasons = seriesData.totalSeasons();
        this.rating = OptionalDouble.of(Double.parseDouble(seriesData.rating())).orElse(0.0);
        this.mainGenre = MediaGenre.fromString(seriesData.genres().split(",")[0].trim());
        this.mainActors = seriesData.mainActors();
        this.synopsis = seriesData.synopsis();
        this.posterUrl = seriesData.posterUrl();
    }

    public String getTitle() {
        return title;
    }

    public Integer getTotalSeasons() {
        return totalSeasons;
    }

    public Double getRating() {
        return rating;
    }

    public MediaGenre getMainGenre() {
        return mainGenre;
    }

    public String getMainActors() {
        return mainActors;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    @Override
    public String toString() {
        return  " Gênero=" + mainGenre +
                ", Título='" + title + '\'' +
                ", Temporadas=" + totalSeasons +
                ", Avaliação=" + rating +
                ", Atores='" + mainActors + '\'' +
                ", Sinopse='" + synopsis + '\'' +
                ", Poster='" + posterUrl + '\'';
    }
}
