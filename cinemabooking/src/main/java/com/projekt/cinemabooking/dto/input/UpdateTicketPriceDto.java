package com.projekt.cinemabooking.dto.input;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTicketPriceDto {
    @NotNull(message = "Cena jest wymagana")
    @Min(value = 0, message = "Cena nie może być ujemna")
    private BigDecimal price;
}