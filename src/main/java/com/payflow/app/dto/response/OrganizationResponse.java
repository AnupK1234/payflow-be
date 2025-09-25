package com.payflow.app.dto.response;

import java.util.List;
import lombok.Data;

@Data
public class OrganizationResponse {
    private Long id;
    private String name;
    private String registrationNumber;
    private String address;
    private String status; // PENDING, VERIFIED, REJECTED
    private String adminUsername;
    private String adminEmail;
    private List<DocumentResponse> documents;

    // Add this field
    private BankAccountResponseDTO bankAccount;
}
