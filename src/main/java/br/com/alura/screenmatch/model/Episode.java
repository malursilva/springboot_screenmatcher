package br.com.alura.screenmatch.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Entity
@Table(name = "episodes")
public class Episode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer seasonNumber;
    private String title;
    private Integer number;
    private Double rating;
    private LocalDate releaseDate;
    @ManyToOne
    private Series series;

    public Episode() {
    }

    public Episode(Integer seasonNumber, EpisodeData episodeData) {
        this.seasonNumber = seasonNumber;
        this.title = episodeData.title();
        this.number = episodeData.number();
        try {
            this.rating = Double.valueOf(episodeData.rating());
        } catch (NumberFormatException e) {
            this.rating = 0.0;
        }
        try {
            this.releaseDate = LocalDate.parse(episodeData.releaseDate());
        } catch (DateTimeParseException e) {
            this.releaseDate = null;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(Integer seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Series getSeries() {
        return series;
    }

    public void setSeries(Series series) {
        this.series = series;
    }

    @Override
    public String toString() {
        return "Season=" + seasonNumber +
                ", Title='" + title + '\'' +
                ", Episode=" + number +
                ", Rating=" + rating +
                ", Release=" + releaseDate;
    }
}