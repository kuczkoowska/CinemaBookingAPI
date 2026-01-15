package com.projekt.cinemabooking.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SalesStatsDto {
    private LocalDate date;
    private int ticketsSold;
    private BigDecimal totalRevenue;
}
