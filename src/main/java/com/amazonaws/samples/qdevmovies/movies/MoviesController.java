package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Fetching movies");
        model.addAttribute("movies", movieService.getAllMovies());
        return "movies";
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }

    /**
     * Search movies endpoint that handles both HTML form submissions and API requests.
     * Returns HTML view for browser requests and JSON for API requests.
     * 
     * @param name Movie name to search for (optional)
     * @param id Movie ID to search for (optional)
     * @param genre Movie genre to search for (optional)
     * @param model Spring UI model for HTML rendering
     * @return HTML view name for browser requests
     */
    @GetMapping("/movies/search")
    public String searchMovies(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre,
            org.springframework.ui.Model model) {
        
        logger.info("Searching movies with parameters - name: {}, id: {}, genre: {}", name, id, genre);
        
        try {
            // Validate ID parameter if provided
            if (id != null && id <= 0) {
                logger.warn("Invalid movie ID provided: {}", id);
                model.addAttribute("title", "Invalid Search Parameters");
                model.addAttribute("message", "Movie ID must be a positive number.");
                return "error";
            }
            
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            // Add search results and parameters to model
            model.addAttribute("movies", searchResults);
            model.addAttribute("searchName", name != null ? name : "");
            model.addAttribute("searchId", id != null ? id.toString() : "");
            model.addAttribute("searchGenre", genre != null ? genre : "");
            model.addAttribute("isSearchResult", true);
            model.addAttribute("searchResultCount", searchResults.size());
            
            logger.info("Found {} movies matching search criteria", searchResults.size());
            return "movies";
            
        } catch (Exception e) {
            logger.error("Error occurred during movie search: {}", e.getMessage(), e);
            model.addAttribute("title", "Search Error");
            model.addAttribute("message", "An error occurred while searching for movies. Please try again.");
            return "error";
        }
    }

    /**
     * API endpoint for movie search that returns JSON response.
     * 
     * @param name Movie name to search for (optional)
     * @param id Movie ID to search for (optional)
     * @param genre Movie genre to search for (optional)
     * @return JSON response with search results
     */
    @GetMapping(value = "/api/movies/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchMoviesApi(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "genre", required = false) String genre) {
        
        logger.info("API search request - name: {}, id: {}, genre: {}", name, id, genre);
        
        try {
            // Validate ID parameter if provided
            if (id != null && id <= 0) {
                logger.warn("Invalid movie ID provided in API request: {}", id);
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "Invalid movie ID. Must be a positive number.");
                errorResponse.put("status", "error");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            Map<String, Object> response = new HashMap<>();
            response.put("movies", searchResults);
            response.put("count", searchResults.size());
            response.put("searchCriteria", Map.of(
                "name", name != null ? name : "",
                "id", id != null ? id : "",
                "genre", genre != null ? genre : ""
            ));
            response.put("status", "success");
            
            logger.info("API search completed successfully, found {} movies", searchResults.size());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error occurred during API movie search: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "An error occurred while searching for movies.");
            errorResponse.put("status", "error");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}