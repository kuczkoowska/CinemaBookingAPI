package com.projekt.cinemabooking.controller.view;

import com.projekt.cinemabooking.dto.input.CreateMovieDto;
import com.projekt.cinemabooking.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/movies")
@RequiredArgsConstructor
public class AdminMovieController {

    private final MovieService movieService;

    // Lista filmów
    @GetMapping
    public String listMovies(Model model) {
        model.addAttribute("movies", movieService.getAllMovies());
        return "admin/movies/list";
    }

    // Formularz dodawania
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("movie", new CreateMovieDto());
        return "admin/movies/form";
    }

    // Akcja zapisu
    @PostMapping
    public String saveMovie(@Valid @ModelAttribute("movie") CreateMovieDto dto, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/movies/form";
        }
        movieService.createMovie(dto);
        return "redirect:/admin/movies";
    }

    // Usuwanie
    @GetMapping("/delete/{id}")
    public String deleteMovie(@PathVariable Long id) {
        try {
            movieService.deleteMovie(id);
        } catch (Exception e) {
        }
        return "redirect:/admin/movies";
    }
}
