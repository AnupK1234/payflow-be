package com.payflow.app.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateDepositRequest {
    @NotNull
    @Min(1)
    private Double amount;
}
