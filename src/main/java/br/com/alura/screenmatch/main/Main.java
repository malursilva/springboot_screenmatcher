package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.model.*;
import br.com.alura.screenmatch.repository.SeriesRepository;
import br.com.alura.screenmatch.service.ApiConsuption;
import br.com.alura.screenmatch.service.DataConverter;

import java.util.*;

public class Main {
    private final String ADDRESS = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    private Scanner reader = new Scanner(System.in);
    private ApiConsuption apiConsuption = new ApiConsuption();
    private DataConverter converter = new DataConverter();
    private SeriesRepository seriesRepository;

    private Optional<Series> seriesCache;

    public Main(SeriesRepository seriesRepository) {
        this.seriesRepository = seriesRepository;
    }

    public void showMenu() {
        var option = -1;
        var entry = "";
        var menu = """
                ---------------------------------
                |  1 - Search TV Series         |
                |  2 - Search Episodes          |
                |  3 - List stored Series       |
                |  4 - List Series by Name      |
                |  5 - List Series by Actor     |
                |  6 - List Series by Genre     |
                |  7 - List Top best rated      |
                |  8 - Filter by season/rating  |
                |  9 - Search episode by name   |
                | 10 - List Top best episodes   |
                |                               |
                |  0 - Exit                     |
                ---------------------------------""";

        do {
            System.out.println(menu);
            entry = reader.nextLine();
            try {
                option = Integer.parseInt(entry);
            } catch (NumberFormatException e) {
                option = -1;
            }

            switch (option) {
                case 1:
                    requestSeries();
                    break;
                case 2:
                    searchEpisodesOnSeriesBySeason();
                    break;
                case 3:
                    listSearchedSeries();
                    break;
                case 4:
                    searchSeriesByTitle();
                    break;
                case 5:
                    searchSeriesByActor();
                    break;
                case 6:
                    searchSeriesByGenre();
                    break;
                case 7:
                    listTopBestRatedSeries();
                    break;
                case 8:
                    filterSeriesBySeasonAndRating();
                    break;
                case 9:
                    searchEpisodeByNameInsert();
                    break;
                case 10:
                    listTopBestRatedEpisodesBySeries();
                    break;
                case 0:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option");
            }
            if (option > 0) {
                System.out.println("\n----------------------------------\n Type anything to return to menu");
                reader.nextLine();
            }
        } while (option != 0);
    }

    private String readStringValue(String message) {
        System.out.println(message);
        return reader.nextLine();
    }

    private void requestSeries() {
        Series series = new Series(getSeriesData());
        seriesRepository.save(series);
        System.out.println(series);
    }

    private SeriesData getSeriesData() {
        var seriesName = readStringValue("Type the TV Series name to search:");
        var json = apiConsuption.getData(ADDRESS + seriesName.replace(" ", "+") + API_KEY);
        return converter.getData(json, SeriesData.class);
    }

    private void searchEpisodesOnSeriesBySeason() {
        listSearchedSeries();
        var seriesName = readStringValue("\nInsert a series name from the list above:");
        Optional<Series> seriesData = seriesRepository.findByTitleContainingIgnoreCase(seriesName);

        if (seriesData.isPresent()) {
            var seriesFound = seriesData.get();
            List<SeasonData> seasonDataList = new ArrayList<>();
            for (int i = 1; i <= seriesFound.getTotalSeasons(); i++) {
                var json = apiConsuption.getData(ADDRESS + seriesFound.getTitle().replace(" ", "+") + "&season=" + i + API_KEY);
                SeasonData seasonData = converter.getData(json, SeasonData.class);
                seasonDataList.add(seasonData);
            }
            seasonDataList.forEach(System.out::println);
            List<Episode> episodes = seasonDataList.stream()
                    .flatMap(d -> d.episodes().stream()
                            .map(e -> new Episode(d.number(), e)))
                    .toList();
            seriesFound.setEpisodes(episodes);
            seriesRepository.save(seriesFound);
        } else {
            System.out.println("Series not found on the database :|");
        }
    }

    private void listSearchedSeries() {
        var allSeries = seriesRepository.findAll();
        allSeries.stream()
                .sorted(Comparator.comparing(Series::getTitle))
                .forEach(System.out::println);
    }

    private void searchSeriesByTitle() {
        var seriesName = readStringValue("Insert a series name to search: ");
        seriesCache = seriesRepository.findByTitleContainingIgnoreCase(seriesName);

        if (seriesCache.isPresent()) {
            System.out.println("Series data: " + seriesCache.get());
        } else {
            System.out.println("Series not found on the database :|");
        }
    }

    private void searchSeriesByActor() {
        var name = readStringValue("Insert the actor/actress name:");
        List<Series> searchedSeries = seriesRepository.findByMainActorsContainingIgnoreCase(name);
        searchedSeries.forEach(s -> System.out.println("Title: " + s.getTitle() + " | Main Cast: " + s.getMainActors()));
    }

    private void searchSeriesByGenre() {
        var genre = readStringValue("Insert a series genre to search:");
        List<Series> searchedSeries = seriesRepository.findByMainGenre(MediaGenre.fromString(genre));
        searchedSeries.forEach(System.out::println);
    }

    private void listTopBestRatedSeries() {
        List<Series> topSeries = seriesRepository.findTop5ByOrderByRatingDesc();
        topSeries.forEach(s -> System.out.println(s.getTitle() + " - " + s.getRating()));
    }

    private void filterSeriesBySeasonAndRating() {
        var maxSeasons = Integer.parseInt(readStringValue("Insert the max number of seasons the series should have:"));
        var minRating = Double.parseDouble(readStringValue("Insert the minimum rating the show should have:"));
        List<Series> searchedSeries = seriesRepository.seriesBySeasonAmountAndRating(maxSeasons, minRating);
        searchedSeries.forEach(s -> System.out.println(s.getTitle() + " | seasons: " + s.getTotalSeasons() + " | rating: " + s.getRating()));
    }

    private void searchEpisodeByNameInsert() {
        var name = readStringValue("Insert the part of the name of the episode:");
        List<Episode> searchedEpisodes = seriesRepository.episodesByPartialName(name);
        searchedEpisodes.forEach(e ->
                System.out.printf("Series: %s | Season: %s | Episode: %s - %s\n",
                        e.getSeries().getTitle(), e.getSeasonNumber(), e.getNumber(), e.getTitle())
        );
    }

    private void listTopBestRatedEpisodesBySeries() {
        searchSeriesByTitle();
        if (seriesCache.isPresent()) {
            var topSize = Integer.parseInt(readStringValue("Insert the Top list size:"));
            List<Episode> topEpisodes = seriesRepository.searchTopEpisodesBySeries(seriesCache.get(), topSize);
            topEpisodes.forEach(e ->
                    System.out.printf("Season: %s | Episode: %s - %s | Rating: %s\n",
                            e.getSeasonNumber(), e.getNumber(), e.getTitle(), e.getRating()));
        }
    }
}
