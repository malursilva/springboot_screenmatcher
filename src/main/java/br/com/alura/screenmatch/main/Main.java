package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.model.SeasonData;
import br.com.alura.screenmatch.model.Series;
import br.com.alura.screenmatch.model.SeriesData;
import br.com.alura.screenmatch.service.ApiConsuption;
import br.com.alura.screenmatch.service.DataConverter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {
    private final String ADDRESS = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    private Scanner reader = new Scanner(System.in);
    private ApiConsuption apiConsuption = new ApiConsuption();
    private DataConverter converter = new DataConverter();

    private List<SeriesData> searchedSeriesList = new ArrayList<>();

    public void showMenu() {
        var option = -1;
        do {
            var menu = """
                                                 \s
                      1 - Buscar séries
                      2 - Buscar episódios
                      3 - Listar séries buscadas
                                                 \s
                      0 - Sair                   \s
                    """;

            System.out.println(menu);
            option = reader.nextInt();
            reader.nextLine();

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
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        } while (option != 0);
    }

    private void requestSeries() {
        SeriesData data = getSeriesData();
        searchedSeriesList.add(data);
        System.out.println(data);
    }

    private SeriesData getSeriesData() {
        System.out.println("Digite o nome da série para busca");
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
        List<Series> series = new ArrayList<>();
        series = searchedSeriesList.stream()
                .map(Series::new)
                .toList();
        series.stream()
                .sorted(Comparator.comparing(Series::getMainGenre))
                .forEach(System.out::println);
    }
}
