package br.com.alura.screenmatch;

import br.com.alura.screenmatch.model.SeasonData;
import br.com.alura.screenmatch.model.SeriesData;
import br.com.alura.screenmatch.service.ApiConsuption;
import br.com.alura.screenmatch.service.DataConverter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		var apiConsuption = new ApiConsuption();
		var json = apiConsuption.getData("https://www.omdbapi.com/?t=gilmore+girls&apikey=6585022c");
		DataConverter converter = new DataConverter();

		SeriesData seriesData = converter.getData(json, SeriesData.class);
		System.out.println(seriesData);
		List<SeasonData> seasonDataList = new ArrayList<>();

		for (int i=1; i <= seriesData.totalSeasons(); i++) {
			json = apiConsuption.getData("https://www.omdbapi.com/?t=gilmore+girls&season=" + i + "&apikey=6585022c");
			seasonDataList.add(converter.getData(json, SeasonData.class));
		}

		seasonDataList.forEach(System.out::println);
	}
}
