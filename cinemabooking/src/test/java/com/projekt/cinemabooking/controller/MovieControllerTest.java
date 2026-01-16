package com.projekt.cinemabooking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projekt.cinemabooking.config.SecurityConfig;
import com.projekt.cinemabooking.dto.input.CreateMovieDto;
import com.projekt.cinemabooking.dto.output.MovieDto;
import com.projekt.cinemabooking.entity.enums.MovieGenre;
import com.projekt.cinemabooking.exception.ResourceNotFoundException;
import com.projekt.cinemabooking.service.MovieService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MovieController.class)
@Import(SecurityConfig.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private MovieService movieService;

    @Test
    @WithMockUser
    @DisplayName("GET /api/movies - Powinien zwrócić stronę filmów")
    void shouldReturnPagedMovies() throws Exception {
        MovieDto movie = new MovieDto();
        movie.setTitle("Dune");
        Page<MovieDto> page = new PageImpl<>(List.of(movie));

        when(movieService.getMovies(any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/movies")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Dune"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/movies - Powinien utworzyć film")
    void shouldCreateMovieAsAdmin() throws Exception {
        CreateMovieDto dto = new CreateMovieDto();
        dto.setTitle("New Movie");
        dto.setDurationMinutes(100);

        dto.setGenre(MovieGenre.AKCJA);
        dto.setDescription("Opis filmu");
        dto.setDirector("Reżyser Testowy");
        dto.setPosterUrl("http://przykladowy-link.pl/obrazek.jpg");

        when(movieService.createMovie(any())).thenReturn(5L);

        mockMvc.perform(post("/api/movies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(content().string("5"));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("POST /api/movies - Powinien zabronić utworzenia filmu (USER)")
    void shouldForbidCreateMovieForUser() throws Exception {
        CreateMovieDto dto = new CreateMovieDto();
        dto.setTitle("New Movie");
        dto.setDurationMinutes(120);
        dto.setGenre(MovieGenre.AKCJA);
        dto.setDescription("Opis");
        dto.setDirector("Reżyser");
        dto.setPosterUrl("http://link.pl");

        mockMvc.perform(post("/api/movies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    @DisplayName("GET /api/movies/{id} - Powinien zwrócić 404 gdy brak filmu")
    void shouldReturn404WhenMovieNotFound() throws Exception {
        when(movieService.getMovieById(99L)).thenThrow(new ResourceNotFoundException("Film", 99L));

        mockMvc.perform(get("/api/movies/99"))
                .andExpect(status().isNotFound());
    }
}