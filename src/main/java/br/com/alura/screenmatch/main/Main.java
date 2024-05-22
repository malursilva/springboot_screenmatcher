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

    private List<Series> currentSavedSeries = new ArrayList<>();

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
                case 0:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option");
            }
            if (option > 0) {
                System.out.println("\nType anything to return to menu");
                reader.nextLine();
            }
        } while (option != 0);
    }

    private void requestSeries() {
        Series series = new Series(getSeriesData());
        seriesRepository.save(series);
        System.out.println(series);
    }

    private SeriesData getSeriesData() {
        System.out.println("Type the TV Series name to search:");
        var seriesName = reader.nextLine();
        var json = apiConsuption.getData(ADDRESS + seriesName.replace(" ", "+") + API_KEY);
        return converter.getData(json, SeriesData.class);
    }

    private void searchEpisodesOnSeriesBySeason() {
        listSearchedSeries();
        System.out.println("Insert a series name from the list above: ");
        var seriesName = reader.nextLine();
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
        currentSavedSeries = seriesRepository.findAll();
        currentSavedSeries.stream()
                .sorted(Comparator.comparing(Series::getTitle))
                .forEach(System.out::println);
    }

    private void searchSeriesByTitle() {
        System.out.println("Insert a series name to search: ");
        var seriesName = reader.nextLine();
        Optional<Series> searchedSeries = seriesRepository.findByTitleContainingIgnoreCase(seriesName);

        if (searchedSeries.isPresent()) {
            System.out.println("Series data: " + searchedSeries.get());
        } else {
            System.out.println("Series not found on the database :|");
        }
    }

    private void searchSeriesByActor() {
        System.out.println("Insert the actor/actress name: ");
        var name = reader.nextLine();
        List<Series> searchedSeries = seriesRepository.findByMainActorsContainingIgnoreCase(name);
        searchedSeries.forEach(s -> System.out.println("Title: " + s.getTitle() + " | Main Cast: " + s.getMainActors()));
    }

    private void searchSeriesByGenre() {
        System.out.println("Insert a series genre to search: ");
        var genre = reader.nextLine();
        List<Series> searchedSeries = seriesRepository.findByMainGenre(MediaGenre.fromString(genre));
        searchedSeries.forEach(System.out::println);
    }

    private void listTopBestRatedSeries() {
        List<Series> topSeries = seriesRepository.findTop5ByOrderByRatingDesc();
        topSeries.forEach(s -> System.out.println(s.getTitle() + " - " + s.getRating()));
    }

    private void filterSeriesBySeasonAndRating() {
        System.out.println("Insert the max number of seasons the series should have:");
        var maxSeasons = reader.nextInt();
        reader.nextLine();
        System.out.println("Insert the minimum rating the show should have:");
        var minRating = reader.nextDouble();
        reader.nextLine();
        List<Series> searchedSeries = seriesRepository.seriesBySeasonAmountAndRating(maxSeasons, minRating);
        searchedSeries.forEach(s -> System.out.println(s.getTitle() + " | seasons: " + s.getTotalSeasons() + " | rating: " + s.getRating()));
    }
}
