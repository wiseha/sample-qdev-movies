package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MoviesController search functionality.
 * Tests both HTML and API search endpoints with various scenarios.
 */
public class MoviesControllerSearchTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services
        mockMovieService = new MovieService() {
            @Override
            public List<Movie> searchMovies(String name, Long id, String genre) {
                // Mock search logic for testing
                if ("Prison".equals(name)) {
                    return Arrays.asList(new Movie(1L, "The Prison Escape", "John Director", 1994, "Drama", "Test description", 142, 5.0));
                } else if (Long.valueOf(1L).equals(id)) {
                    return Arrays.asList(new Movie(1L, "The Prison Escape", "John Director", 1994, "Drama", "Test description", 142, 5.0));
                } else if ("Drama".equals(genre)) {
                    return Arrays.asList(
                        new Movie(1L, "The Prison Escape", "John Director", 1994, "Drama", "Test description", 142, 5.0),
                        new Movie(5L, "Life Journey", "Robert Filmmaker", 1994, "Drama/Romance", "Test description", 142, 4.0)
                    );
                } else if ("NonExistent".equals(name) || "Horror".equals(genre) || Long.valueOf(999L).equals(id)) {
                    return Arrays.asList(); // Empty list for no matches
                } else {
                    // Return all movies for null/empty criteria
                    return Arrays.asList(
                        new Movie(1L, "The Prison Escape", "John Director", 1994, "Drama", "Test description", 142, 5.0),
                        new Movie(2L, "The Family Boss", "Michael Filmmaker", 1972, "Crime/Drama", "Test description", 175, 5.0)
                    );
                }
            }
        };
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return Arrays.asList();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    // HTML Search Endpoint Tests

    @Test
    public void testSearchMovies_ByName_Success() {
        String result = moviesController.searchMovies("Prison", null, null, model);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        assertTrue(model.containsAttribute("isSearchResult"));
        assertTrue(model.containsAttribute("searchResultCount"));
        assertEquals("Prison", model.getAttribute("searchName"));
        assertEquals("", model.getAttribute("searchId"));
        assertEquals("", model.getAttribute("searchGenre"));
        assertEquals(1, model.getAttribute("searchResultCount"));
    }

    @Test
    public void testSearchMovies_ById_Success() {
        String result = moviesController.searchMovies(null, 1L, null, model);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        assertEquals("", model.getAttribute("searchName"));
        assertEquals("1", model.getAttribute("searchId"));
        assertEquals("", model.getAttribute("searchGenre"));
        assertEquals(1, model.getAttribute("searchResultCount"));
    }

    @Test
    public void testSearchMovies_ByGenre_Success() {
        String result = moviesController.searchMovies(null, null, "Drama", model);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        assertEquals("", model.getAttribute("searchName"));
        assertEquals("", model.getAttribute("searchId"));
        assertEquals("Drama", model.getAttribute("searchGenre"));
        assertEquals(2, model.getAttribute("searchResultCount"));
    }

    @Test
    public void testSearchMovies_NoResults() {
        String result = moviesController.searchMovies("NonExistent", null, null, model);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        assertEquals(0, model.getAttribute("searchResultCount"));
        assertEquals("NonExistent", model.getAttribute("searchName"));
    }

    @Test
    public void testSearchMovies_AllParametersNull() {
        String result = moviesController.searchMovies(null, null, null, model);
        
        assertEquals("movies", result);
        assertTrue(model.containsAttribute("movies"));
        assertEquals("", model.getAttribute("searchName"));
        assertEquals("", model.getAttribute("searchId"));
        assertEquals("", model.getAttribute("searchGenre"));
        assertEquals(2, model.getAttribute("searchResultCount")); // Mock returns 2 movies for null criteria
    }

    @Test
    public void testSearchMovies_InvalidId_NegativeNumber() {
        String result = moviesController.searchMovies(null, -1L, null, model);
        
        assertEquals("error", result);
        assertTrue(model.containsAttribute("title"));
        assertTrue(model.containsAttribute("message"));
        assertEquals("Invalid Search Parameters", model.getAttribute("title"));
    }

    @Test
    public void testSearchMovies_InvalidId_Zero() {
        String result = moviesController.searchMovies(null, 0L, null, model);
        
        assertEquals("error", result);
        assertTrue(model.containsAttribute("title"));
        assertTrue(model.containsAttribute("message"));
        assertEquals("Invalid Search Parameters", model.getAttribute("title"));
    }

    @Test
    public void testSearchMovies_CombinedParameters() {
        String result = moviesController.searchMovies("Prison", 1L, "Drama", model);
        
        assertEquals("movies", result);
        assertEquals("Prison", model.getAttribute("searchName"));
        assertEquals("1", model.getAttribute("searchId"));
        assertEquals("Drama", model.getAttribute("searchGenre"));
    }

    // API Search Endpoint Tests

    @Test
    public void testSearchMoviesApi_ByName_Success() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi("Prison", null, null);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("success", body.get("status"));
        assertEquals(1, body.get("count"));
        assertTrue(body.containsKey("movies"));
        assertTrue(body.containsKey("searchCriteria"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> searchCriteria = (Map<String, Object>) body.get("searchCriteria");
        assertEquals("Prison", searchCriteria.get("name"));
        assertEquals("", searchCriteria.get("id"));
        assertEquals("", searchCriteria.get("genre"));
    }

    @Test
    public void testSearchMoviesApi_ById_Success() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi(null, 1L, null);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("success", body.get("status"));
        assertEquals(1, body.get("count"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> searchCriteria = (Map<String, Object>) body.get("searchCriteria");
        assertEquals("", searchCriteria.get("name"));
        assertEquals(1L, searchCriteria.get("id"));
        assertEquals("", searchCriteria.get("genre"));
    }

    @Test
    public void testSearchMoviesApi_ByGenre_Success() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi(null, null, "Drama");
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("success", body.get("status"));
        assertEquals(2, body.get("count"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> searchCriteria = (Map<String, Object>) body.get("searchCriteria");
        assertEquals("", searchCriteria.get("name"));
        assertEquals("", searchCriteria.get("id"));
        assertEquals("Drama", searchCriteria.get("genre"));
    }

    @Test
    public void testSearchMoviesApi_NoResults() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi("NonExistent", null, null);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("success", body.get("status"));
        assertEquals(0, body.get("count"));
        assertTrue(body.containsKey("movies"));
        
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) body.get("movies");
        assertTrue(movies.isEmpty());
    }

    @Test
    public void testSearchMoviesApi_AllParametersNull() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi(null, null, null);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("success", body.get("status"));
        assertEquals(2, body.get("count")); // Mock returns 2 movies for null criteria
        
        @SuppressWarnings("unchecked")
        Map<String, Object> searchCriteria = (Map<String, Object>) body.get("searchCriteria");
        assertEquals("", searchCriteria.get("name"));
        assertEquals("", searchCriteria.get("id"));
        assertEquals("", searchCriteria.get("genre"));
    }

    @Test
    public void testSearchMoviesApi_InvalidId_NegativeNumber() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi(null, -1L, null);
        
        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("error", body.get("status"));
        assertTrue(body.containsKey("error"));
        assertTrue(body.get("error").toString().contains("Invalid movie ID"));
    }

    @Test
    public void testSearchMoviesApi_InvalidId_Zero() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi(null, 0L, null);
        
        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("error", body.get("status"));
        assertTrue(body.containsKey("error"));
    }

    @Test
    public void testSearchMoviesApi_CombinedParameters() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi("Prison", 1L, "Drama");
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("success", body.get("status"));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> searchCriteria = (Map<String, Object>) body.get("searchCriteria");
        assertEquals("Prison", searchCriteria.get("name"));
        assertEquals(1L, searchCriteria.get("id"));
        assertEquals("Drama", searchCriteria.get("genre"));
    }

    @Test
    public void testSearchMoviesApi_EmptyStringParameters() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi("", null, "");
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        
        Map<String, Object> body = response.getBody();
        assertEquals("success", body.get("status"));
        assertEquals(2, body.get("count")); // Should treat empty strings as null
    }
}