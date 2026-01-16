package com.projekt.cinemabooking.controller.view;

import com.projekt.cinemabooking.dto.input.CreateScreeningDto;
import com.projekt.cinemabooking.repository.TheaterRoomRepository;
import com.projekt.cinemabooking.service.MovieService;
import com.projekt.cinemabooking.service.ScreeningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/screenings")
@RequiredArgsConstructor
public class AdminScreeningController {

    private final ScreeningService screeningService;
    private final MovieService movieService;
    private final TheaterRoomRepository theaterRoomRepository;

    @GetMapping
    public String listScreenings(Model model) {
        model.addAttribute("screenings", screeningService.getAllScreenings());
        return "admin/screenings/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("screening", new CreateScreeningDto());
        prepareFormData(model);
        return "admin/screenings/form";
    }

    @PostMapping
    public String createScreening(@Valid @ModelAttribute("screening") CreateScreeningDto dto,
                                  BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            prepareFormData(model);
            return "admin/screenings/form";
        }

        try {
            screeningService.createScreening(dto);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage());
            prepareFormData(model);
            return "admin/screenings/form";
        }

        return "redirect:/admin/screenings";
    }

    private void prepareFormData(Model model) {
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("rooms", theaterRoomRepository.findAll());
    }
}