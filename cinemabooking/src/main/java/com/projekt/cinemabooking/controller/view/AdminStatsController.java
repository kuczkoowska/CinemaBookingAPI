package com.projekt.cinemabooking.controller.view;

import com.projekt.cinemabooking.dto.output.SalesStatsDto;
import com.projekt.cinemabooking.repository.SalesStatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/stats")
@RequiredArgsConstructor
public class AdminStatsController {

    private final SalesStatisticsRepository statsRepository;

    @GetMapping
    public String showStats(Model model,
                            @RequestParam(defaultValue = "date") String sortBy,
                            @RequestParam(defaultValue = "DESC") String dir) {

        List<SalesStatsDto> stats = statsRepository.getSalesByDate(sortBy, dir);
        model.addAttribute("stats", stats);

        BigDecimal totalRevenue = stats.stream()
                .map(SalesStatsDto::getTotalRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("totalRevenue", totalRevenue);

        return "admin/stats/view";
    }
}