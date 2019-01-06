package controllers;

import models.DataSource;
import models.Movie;
import models.MovieResponse;
import utils.JsonParser;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/movies")
public class MovieController extends HttpServlet {

    private static final int PAGE_SIZE = 20;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String paramPage = req.getParameter("page");
        int page = 1;
        if (paramPage != null && !paramPage.isEmpty()) {
            page = Integer.parseInt(paramPage);
        }

        int counts = DataSource.getInstance().countMovies();
        List<Movie> movies = DataSource.getInstance().getMovies(page, PAGE_SIZE);

        MovieResponse movieResp = new MovieResponse();
        movieResp.setPage(page);
        movieResp.setTotalPages((int) Math.ceil(counts / (float) PAGE_SIZE));
        movieResp.setResults(movies);

        String json = JsonParser.toJSONString(movieResp);
        resp.setContentType("application/json");
        resp.getWriter().write(json);
    }
}
