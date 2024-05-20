package br.com.alura.screenmatch.main;

import br.com.alura.screenmatch.model.EpisodeData;
import br.com.alura.screenmatch.model.SeasonData;
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
        List<EpisodeData> allEpisodesData = new ArrayList<>();
        allEpisodesData = seasonsList.stream().flatMap(t -> t.episodes().stream()).collect(Collectors.toList());
        System.out.println("\n -- Top 5 Episódios --");
        allEpisodesData.stream()
                .filter(e -> !e.rating().equalsIgnoreCase("N/A"))
                .sorted(Comparator.comparing(EpisodeData::rating).reversed())
                .limit(5)
                .forEach(System.out::println);


    }
}
