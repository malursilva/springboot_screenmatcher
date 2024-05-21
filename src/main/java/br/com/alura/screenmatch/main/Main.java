package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.model.Episode;
import br.com.alura.screenmatch.model.EpisodeData;
import br.com.alura.screenmatch.model.SeasonData;
import br.com.alura.screenmatch.model.SeriesData;
import br.com.alura.screenmatch.service.ApiConsuption;
import br.com.alura.screenmatch.service.DataConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private final String ADDRESS = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    private Scanner reader = new Scanner(System.in);
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private ApiConsuption apiConsuption = new ApiConsuption();
    private DataConverter converter = new DataConverter();

    public void showMenu() {
        System.out.println("Digite o nome da série para buscar: ");
        var serieName = reader.nextLine().replace(" ", "+");
        var json = apiConsuption.getData(ADDRESS + serieName + API_KEY);

        SeriesData seriesData = converter.getData(json, SeriesData.class);
        System.out.println(seriesData);

        List<SeasonData> seasonsList = new ArrayList<>();
		for (int i=1; i <= seriesData.totalSeasons(); i++) {
			json = apiConsuption.getData(ADDRESS + serieName + "&season=" +i + API_KEY);
			seasonsList.add(converter.getData(json, SeasonData.class));
        }

        System.out.println("\n -- Top 5 Episódios --");
        List<EpisodeData> allEpisodesData;
        allEpisodesData = seasonsList.stream().flatMap(t -> t.episodes().stream()).collect(Collectors.toList());
        allEpisodesData.stream()
                .filter(e -> !e.rating().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(EpisodeData::rating).reversed())
                .limit(5)
                .map(e -> e.title().toUpperCase())
                .forEach(System.out::println);

        List<Episode> episodes;
        episodes = seasonsList.stream()
                .flatMap(t -> t.episodes().stream()
                        .map(d -> new Episode(t.number(), d))
                )
                .toList();
        episodes.forEach(System.out::println);

        System.out.println("Digite o título do episódio que está buscando");
        var searchTitle = reader.nextLine();
        Optional<Episode> searchedEpisode = episodes.stream()
                .filter(e -> e.getTitle().toUpperCase().contains(searchTitle.toUpperCase()))
                .findFirst();

        if(searchedEpisode.isPresent()) {
            System.out.println("Episódio encontrado: " + searchedEpisode);
        } else {
            System.out.println("Episódio não encontrado :(");
        }

        System.out.println("A partir de qual ano deseja ver os episódios?");
        var year = reader.nextInt();
        reader.nextLine();
        LocalDate searchDate = LocalDate.of(year, 1, 1);

        episodes.stream()
                .filter(e -> e.getReleaseDate() != null && e.getReleaseDate().isAfter(searchDate))
                .forEach(e -> System.out.println(
                        "Temporada: " + e.getSeasonNumber() +
                                " | Episódio: " + e.getNumber() +
                                " | Data de Lançamento" + e.getReleaseDate().format(dateTimeFormatter)
                ));

        DoubleSummaryStatistics estSeasonRating = episodes.stream()
                .filter(e -> e.getRating() > 0.0)
                .collect(Collectors.summarizingDouble(Episode::getRating));
        System.out.println("Média: " + estSeasonRating.getAverage() +
                " | Pior: " + estSeasonRating.getMin() +
                " | Melhor: " + estSeasonRating.getMax());
    }
}
