package com.payflow.app.dto.request;

import lombok.Data;

@Data
public class UpdateOrganizationRequest {
    private String name;
    private String registrationNumber;
    private String address;

    // Add this line
    private BankAccountRequestDTO bankAccount;
}
