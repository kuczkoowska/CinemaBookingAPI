package com.projekt.cinemabooking.dto.input;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConfirmBookingDto {
    private ContactDataDto contact;
    private Boolean wantsInvoice;
    private InvoiceDataDto invoice;
    private List<HolderDataDto> holders;
}
