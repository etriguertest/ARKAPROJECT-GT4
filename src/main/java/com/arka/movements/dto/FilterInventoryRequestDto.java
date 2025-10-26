package com.arka.movements.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FilterInventoryRequestDto {
    private String status;
    private LocalDate starDate;
    private LocalDate finishDate;
    private Long branchId;
}
