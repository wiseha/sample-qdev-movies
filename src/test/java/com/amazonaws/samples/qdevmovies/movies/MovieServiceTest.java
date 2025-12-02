package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MovieService class.
 * Tests movie search functionality and edge cases.
 */
public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    public void testGetAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        // Verify we have the expected number of movies from movies.json
        assertEquals(12, movies.size());
    }

    @Test
    public void testGetMovieById_ValidId() {
        Optional<Movie> movie = movieService.getMovieById(1L);
        assertTrue(movie.isPresent());
        assertEquals("The Prison Escape", movie.get().getMovieName());
        assertEquals(1L, movie.get().getId());
    }

    @Test
    public void testGetMovieById_InvalidId() {
        Optional<Movie> movie = movieService.getMovieById(999L);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieById_NullId() {
        Optional<Movie> movie = movieService.getMovieById(null);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieById_ZeroId() {
        Optional<Movie> movie = movieService.getMovieById(0L);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieById_NegativeId() {
        Optional<Movie> movie = movieService.getMovieById(-1L);
        assertFalse(movie.isPresent());
    }

    // Search functionality tests

    @Test
    public void testSearchMovies_ByName_ExactMatch() {
        List<Movie> results = movieService.searchMovies("The Prison Escape", null, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMovies_ByName_PartialMatch() {
        List<Movie> results = movieService.searchMovies("Prison", null, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMovies_ByName_CaseInsensitive() {
        List<Movie> results = movieService.searchMovies("prison", null, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMovies_ByName_MultipleResults() {
        List<Movie> results = movieService.searchMovies("The", null, null);
        assertTrue(results.size() > 1);
        // Should find multiple movies with "The" in the name
        assertTrue(results.stream().anyMatch(m -> m.getMovieName().contains("The")));
    }

    @Test
    public void testSearchMovies_ByName_NoMatch() {
        List<Movie> results = movieService.searchMovies("NonExistentMovie", null, null);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMovies_ByName_EmptyString() {
        List<Movie> results = movieService.searchMovies("", null, null);
        assertEquals(12, results.size()); // Should return all movies
    }

    @Test
    public void testSearchMovies_ByName_NullString() {
        List<Movie> results = movieService.searchMovies(null, null, null);
        assertEquals(12, results.size()); // Should return all movies
    }

    @Test
    public void testSearchMovies_ByName_WhitespaceOnly() {
        List<Movie> results = movieService.searchMovies("   ", null, null);
        assertEquals(12, results.size()); // Should return all movies
    }

    @Test
    public void testSearchMovies_ById_ValidId() {
        List<Movie> results = movieService.searchMovies(null, 1L, null);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMovies_ById_InvalidId() {
        List<Movie> results = movieService.searchMovies(null, 999L, null);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMovies_ById_NullId() {
        List<Movie> results = movieService.searchMovies(null, null, null);
        assertEquals(12, results.size()); // Should return all movies
    }

    @Test
    public void testSearchMovies_ByGenre_ExactMatch() {
        List<Movie> results = movieService.searchMovies(null, null, "Drama");
        assertTrue(results.size() > 0);
        assertTrue(results.stream().allMatch(m -> m.getGenre().contains("Drama")));
    }

    @Test
    public void testSearchMovies_ByGenre_PartialMatch() {
        List<Movie> results = movieService.searchMovies(null, null, "Crime");
        assertTrue(results.size() > 0);
        assertTrue(results.stream().allMatch(m -> m.getGenre().toLowerCase().contains("crime")));
    }

    @Test
    public void testSearchMovies_ByGenre_CaseInsensitive() {
        List<Movie> results = movieService.searchMovies(null, null, "drama");
        assertTrue(results.size() > 0);
        assertTrue(results.stream().allMatch(m -> m.getGenre().toLowerCase().contains("drama")));
    }

    @Test
    public void testSearchMovies_ByGenre_NoMatch() {
        List<Movie> results = movieService.searchMovies(null, null, "Horror");
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMovies_ByGenre_EmptyString() {
        List<Movie> results = movieService.searchMovies(null, null, "");
        assertEquals(12, results.size()); // Should return all movies
    }

    @Test
    public void testSearchMovies_ByGenre_NullString() {
        List<Movie> results = movieService.searchMovies(null, null, null);
        assertEquals(12, results.size()); // Should return all movies
    }

    @Test
    public void testSearchMovies_CombinedCriteria_NameAndGenre() {
        List<Movie> results = movieService.searchMovies("Family", null, "Crime");
        assertEquals(1, results.size());
        assertEquals("The Family Boss", results.get(0).getMovieName());
        assertTrue(results.get(0).getGenre().contains("Crime"));
    }

    @Test
    public void testSearchMovies_CombinedCriteria_NameAndId() {
        List<Movie> results = movieService.searchMovies("Prison", 1L, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
        assertEquals(1L, results.get(0).getId());
    }

    @Test
    public void testSearchMovies_CombinedCriteria_IdAndGenre() {
        List<Movie> results = movieService.searchMovies(null, 2L, "Crime");
        assertEquals(1, results.size());
        assertEquals("The Family Boss", results.get(0).getMovieName());
        assertEquals(2L, results.get(0).getId());
    }

    @Test
    public void testSearchMovies_CombinedCriteria_AllThree() {
        List<Movie> results = movieService.searchMovies("Family", 2L, "Crime");
        assertEquals(1, results.size());
        assertEquals("The Family Boss", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMovies_CombinedCriteria_NoMatch() {
        List<Movie> results = movieService.searchMovies("Prison", 2L, null); // Wrong ID for Prison movie
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMovies_CombinedCriteria_ConflictingCriteria() {
        List<Movie> results = movieService.searchMovies("Prison", null, "Action"); // Prison movie is Drama, not Action
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMovies_WithTrimming() {
        List<Movie> results = movieService.searchMovies("  Prison  ", null, "  Drama  ");
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }
}