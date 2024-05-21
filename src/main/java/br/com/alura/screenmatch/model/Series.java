package br.com.alura.screenmatch.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;

@Entity
public class Series {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String title;
    private Integer totalSeasons;
    private Double rating;
    @Enumerated(EnumType.STRING)
    private MediaGenre mainGenre;
    private String mainActors;
    private String synopsis;
    private String posterUrl;
    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Episode> episodes = new ArrayList<>();

    public Series() {
    }

    public Series(SeriesData seriesData) {
        this.title = seriesData.title();
        this.totalSeasons = seriesData.totalSeasons();
        this.rating = OptionalDouble.of(Double.parseDouble(seriesData.rating())).orElse(0.0);
        this.mainGenre = MediaGenre.fromString(seriesData.genres().split(",")[0].trim());
        this.mainActors = seriesData.mainActors();
        this.synopsis = seriesData.synopsis();
        this.posterUrl = seriesData.posterUrl();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTotalSeasons() {
        return totalSeasons;
    }

    public void setTotalSeasons(Integer totalSeasons) {
        this.totalSeasons = totalSeasons;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public MediaGenre getMainGenre() {
        return mainGenre;
    }

    public void setMainGenre(MediaGenre mainGenre) {
        this.mainGenre = mainGenre;
    }

    public String getMainActors() {
        return mainActors;
    }

    public void setMainActors(String mainActors) {
        this.mainActors = mainActors;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void setEpisodes(List<Episode> episodes) {
        episodes.forEach(e -> e.setSeries(this));
        this.episodes = episodes;
    }

    @Override
    public String toString() {
        return " Genre=" + mainGenre +
                ", Title='" + title + '\'' +
                ", Seasons=" + totalSeasons +
                ", Rating=" + rating +
                ", Actors='" + mainActors + '\'' +
                ", Plot='" + synopsis + '\'' +
                ", Poster='" + posterUrl + '\'';
    }
}
