package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.model.SeasonData;
import br.com.alura.screenmatch.model.Series;
import br.com.alura.screenmatch.model.SeriesData;
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

    public Main(SeriesRepository seriesRepository) {
        this.seriesRepository = seriesRepository;
    }

    public void showMenu() {
        var option = -1;
        var entry = "";
        var menu = """
                                                 \s
                      1 - Search TV Series
                      2 - Search Episodes
                      3 - List searched Series
                                                 \s
                      0 - Exit                   \s
                    """;

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
                case 0:
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option");
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
        SeriesData seriesData = getSeriesData();
        List<SeasonData> seasonDataList = new ArrayList<>();

        for (int i = 1; i <= seriesData.totalSeasons(); i++) {
            var json = apiConsuption.getData(ADDRESS + seriesData.title().replace(" ", "+") + "&season=" + i + API_KEY);
            SeasonData seasonData = converter.getData(json, SeasonData.class);
            seasonDataList.add(seasonData);
        }
        seasonDataList.forEach(System.out::println);
    }

    private void listSearchedSeries() {
        List<Series> series = seriesRepository.findAll();
        series.stream()
                .sorted(Comparator.comparing(Series::getMainGenre))
                .forEach(System.out::println);
    }
}
