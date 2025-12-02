# Movie Service - Spring Boot Demo Application

A simple movie catalog web application built with Spring Boot, demonstrating Java application development best practices.

## Features

- **Movie Catalog**: Browse 12 classic movies with detailed information
- **Movie Search & Filtering**: Search movies by name, ID, or genre with real-time results
- **Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **REST API**: JSON API endpoints for programmatic access to movie data
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Dark theme with gradient backgrounds and smooth animations
- **Pirate-themed Messages**: Fun pirate language for empty search results! 🏴‍☠️

## Technology Stack

- **Java 8**
- **Spring Boot 2.0.5**
- **Maven** for dependency management
- **Thymeleaf** for server-side templating
- **Log4j 2.20.0**
- **JUnit 5.8.2**

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie List**: http://localhost:8080/movies
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **Movie Search**: http://localhost:8080/movies/search?name=Prison&genre=Drama

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── movies/
│   │       │   ├── MoviesApplication.java    # Main Spring Boot application
│   │       │   ├── MoviesController.java     # REST controller for movie endpoints
│   │       │   ├── MovieService.java         # Business logic for movie operations
│   │       │   ├── Movie.java                # Movie data model
│   │       │   ├── Review.java               # Review data model
│   │       │   └── ReviewService.java        # Review business logic
│   │       └── utils/
│   │           ├── MovieIconUtils.java       # Movie icon utilities
│   │           └── MovieUtils.java           # Movie validation utilities
│   └── resources/
│       ├── application.yml                   # Application configuration
│       ├── movies.json                       # Movie catalog data
│       ├── mock-reviews.json                 # Mock review data
│       ├── log4j2.xml                        # Logging configuration
│       ├── templates/                        # Thymeleaf HTML templates
│       │   ├── movies.html                   # Movie list with search form
│       │   ├── movie-details.html            # Movie detail page
│       │   └── error.html                    # Error page template
│       └── static/css/                       # CSS stylesheets
│           ├── movies.css                    # Main stylesheet
│           └── movie-details.css             # Detail page styles
└── test/                                     # Unit tests
    └── com/amazonaws/samples/qdevmovies/movies/
        ├── MovieServiceTest.java             # Service layer tests
        ├── MoviesControllerTest.java         # Controller tests
        └── MoviesControllerSearchTest.java   # Search functionality tests
```

## API Endpoints

### Web Interface Endpoints

#### Get All Movies
```
GET /movies
```
Returns an HTML page displaying all movies with ratings, basic information, and a search form.

#### Search Movies (HTML)
```
GET /movies/search
```
Returns an HTML page with filtered movie results based on search criteria.

**Query Parameters:**
- `name` (optional): Movie name to search for (partial match, case-insensitive)
- `id` (optional): Movie ID to search for (exact match)
- `genre` (optional): Movie genre to search for (partial match, case-insensitive)

**Examples:**
```
http://localhost:8080/movies/search?name=Prison
http://localhost:8080/movies/search?genre=Drama
http://localhost:8080/movies/search?id=1
http://localhost:8080/movies/search?name=Family&genre=Crime
```

#### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

### REST API Endpoints

#### Search Movies (JSON API)
```
GET /api/movies/search
```
Returns JSON response with filtered movie results based on search criteria.

**Query Parameters:**
- `name` (optional): Movie name to search for (partial match, case-insensitive)
- `id` (optional): Movie ID to search for (exact match)
- `genre` (optional): Movie genre to search for (partial match, case-insensitive)

**Response Format:**
```json
{
  "status": "success",
  "count": 2,
  "movies": [
    {
      "id": 1,
      "movieName": "The Prison Escape",
      "director": "John Director",
      "year": 1994,
      "genre": "Drama",
      "description": "Two imprisoned men bond over a number of years...",
      "duration": 142,
      "imdbRating": 5.0
    }
  ],
  "searchCriteria": {
    "name": "Prison",
    "id": "",
    "genre": ""
  }
}
```

**Error Response:**
```json
{
  "status": "error",
  "error": "Invalid movie ID. Must be a positive number."
}
```

**Examples:**
```bash
curl "http://localhost:8080/api/movies/search?name=Prison"
curl "http://localhost:8080/api/movies/search?genre=Drama&name=Family"
curl "http://localhost:8080/api/movies/search?id=1"
```

## Search Features

### Search Capabilities
- **Name Search**: Partial, case-insensitive matching on movie titles
- **Genre Search**: Partial, case-insensitive matching on movie genres
- **ID Search**: Exact matching on movie IDs
- **Combined Search**: Use multiple criteria simultaneously for precise filtering
- **Empty Results Handling**: Friendly pirate-themed messages when no movies match

### Search Examples
- Search for "Prison" → finds "The Prison Escape"
- Search for "Crime" → finds all movies with "Crime" in genre
- Search for ID "1" → finds the movie with ID 1
- Search for "Family" + "Crime" → finds "The Family Boss"

### Edge Cases Handled
- Invalid movie IDs (negative numbers, zero)
- Empty search results with helpful suggestions
- Null or empty search parameters (returns all movies)
- Whitespace-only search terms
- Case-insensitive matching
- Parameter trimming

## Testing

Run the test suite:
```bash
mvn test
```

### Test Coverage
- **MovieServiceTest**: Tests search functionality, edge cases, and data validation
- **MoviesControllerTest**: Tests existing controller functionality
- **MoviesControllerSearchTest**: Tests search endpoints (both HTML and API)

Test scenarios include:
- Valid and invalid search parameters
- Empty search results
- Combined search criteria
- Error handling and validation
- API response formats

## Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

1. Verify the application is running on the correct port
2. Check that movies.json is properly loaded (check logs)
3. Ensure search parameters are properly URL-encoded

## Contributing

This project is designed as a demonstration application. Feel free to:
- Add more movies to the catalog
- Enhance the search functionality (e.g., year-based filtering)
- Improve the UI/UX with additional features
- Add more sophisticated filtering options
- Implement user authentication and favorites

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.
