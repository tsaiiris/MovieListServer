package models;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DataSource {

    private static DataSource instance;
    private Connection connection;

    private DataSource() {
        Properties props = new Properties();
        readProperties(props, "database.properties");

        try {
            Class.forName("com.mysql.jdbc.Driver");

            connection = DriverManager.getConnection(
                    props.getProperty("database.connection"),
                    props.getProperty("database.username"),
                    props.getProperty("database.password")
            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static DataSource getInstance() {
        if (instance == null) {
            synchronized (DataSource.class) {
                if (instance == null) {
                    instance = new DataSource();
                }
            }
        }

        return instance;
    }

    /*----------------------------------------------------------------------------------------------------------------*/
    /* Public APIs */
    public int newMovies(List<Movie> movies) {
        setAutoCommit(false);

        String query = "INSERT INTO movie (id, title, overview, vote_count, vote_average, poster_path, release_date) values (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {

            for (Movie movie : movies) {
                statement.setInt(1, movie.getId());
                statement.setString(2, movie.getTitle());
                statement.setString(3, movie.getDescription());
                statement.setInt(4, movie.getVoteCount());
                statement.setDouble(5, movie.getVoteAverage());
                statement.setString(6, "https://image.tmdb.org/t/p/w500" + movie.getPosterPath());
                statement.setString(7, movie.getReleaseDate());

                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();

            return movies.size();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setAutoCommit(true);
        return 0;
    }

    public List<Movie> getMovies(int page, int pageSize) {
        if (page <= 0 || pageSize <= 0) {
            return new ArrayList<>();
        }

        List<Movie> movies = new ArrayList<>();
        String query = "SELECT * FROM movie WHERE seq >= ? and seq < ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, (page - 1) * pageSize + 1);
            statement.setInt(2, page * pageSize + 1);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Movie movie = new Movie();

                movie.setSeq(resultSet.getInt("seq"));
                movie.setId(resultSet.getInt("id"));
                movie.setTitle(resultSet.getString("title"));
                movie.setDescription(resultSet.getString("overview"));
                movie.setVoteCount(resultSet.getInt("vote_count"));
                movie.setVoteAverage(resultSet.getDouble("vote_average"));
                movie.setPosterPath(resultSet.getString("poster_path"));
                movie.setReleaseDate(resultSet.getString("release_date"));

                movies.add(movie);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movies;
    }

    public int countMovies() {
        String query = "SELECT COUNT(seq) FROM movie";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            resultSet.next();
            return (int) resultSet.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /*----------------------------------------------------------------------------------------------------------------*/
    /* Internal helpers */
    private void readProperties(Properties props, String path) {
        URL url = getClass().getClassLoader().getResource(path);

        try (FileReader reader = new FileReader(url.getFile())) {
            props.load(reader);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private boolean setAutoCommit(boolean enabled) {
        try {
            connection.setAutoCommit(enabled);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
