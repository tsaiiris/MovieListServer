package scrawler;

import models.DataSource;
import models.Movie;
import models.MovieResponse;
import utils.JsonParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Scrawler {

    public static void main(String[] args) {
        int from = 1;
        int to = 989;

        for (int i = from; i <= to; i++) {
            List<Movie> movies = fetchMovies(i);
            int res = DataSource.getInstance().newMovies(movies);
            if (res == 0) {
                break;
            }
        }
    }

    private static List<Movie> fetchMovies(int page) {
        URL url;
        InputStreamReader reader = null;

        try {
            url = new URL("https://api.themoviedb.org/3/movie/popular?api_key=4bef8838c2fd078bd13d7127d8dedcd4&language=en-US&page=" + page);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(3000);
            connection.setConnectTimeout(3000);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoInput(true);

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                reader = new InputStreamReader(connection.getInputStream());
                MovieResponse resp = JsonParser.parse(reader, MovieResponse.class);
                return resp.getResults();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }

        return new ArrayList<>();
    }
}
