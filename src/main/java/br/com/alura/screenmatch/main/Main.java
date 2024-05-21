package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.model.SeasonData;
import br.com.alura.screenmatch.model.SeriesData;
import br.com.alura.screenmatch.service.ApiConsuption;
import br.com.alura.screenmatch.service.DataConverter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private final String ADDRESS = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    private Scanner reader = new Scanner(System.in);
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private ApiConsuption apiConsuption = new ApiConsuption();
    private DataConverter converter = new DataConverter();

    public void showMenu() {
        var menu = """
                1 - Buscar séries
                2 - Buscar episódios
                                
                0 - Sair                                 
                """;

        System.out.println(menu);
        var option = reader.nextInt();
        reader.nextLine();

        switch (option) {
            case 1:
                requestSeries();
                break;
            case 2:
                searchEpisodesOnSeriesBySeason();
                break;
            case 0:
                System.out.println("Saindo...");
                break;
            default:
                System.out.println("Opção inválida");
        }
    }

    private void requestSeries() {
        SeriesData data = getSeriesData();
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
}
