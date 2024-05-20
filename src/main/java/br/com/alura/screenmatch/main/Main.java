package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.model.SeasonData;
import br.com.alura.screenmatch.model.SeriesData;
import br.com.alura.screenmatch.service.ApiConsuption;
import br.com.alura.screenmatch.service.DataConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private final String ADDRESS = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    private Scanner reader = new Scanner(System.in);
    private ApiConsuption apiConsuption = new ApiConsuption();
    private DataConverter converter = new DataConverter();

    public void showMenu() {
        System.out.println("Digite o nome da série para buscar: ");
        var serieName = reader.nextLine().replace(" ", "+");
        var json = apiConsuption.getData(ADDRESS + serieName + API_KEY);

        SeriesData seriesData = converter.getData(json, SeriesData.class);
        System.out.println(seriesData);

        List<SeasonData> seasonDataList = new ArrayList<>();
		for (int i=1; i <= seriesData.totalSeasons(); i++) {
			json = apiConsuption.getData(ADDRESS + serieName + "&season=" + i + API_KEY);
			seasonDataList.add(converter.getData(json, SeasonData.class));
		}
		seasonDataList.forEach(System.out::println);
    }
}
