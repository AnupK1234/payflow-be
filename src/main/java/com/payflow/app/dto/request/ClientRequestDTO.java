
package com.payflow.app.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientRequestDTO {

    @NotNull
    @Size(max = 100)
    private String companyName;

    @NotNull
    @Size(max = 100)
    private String contactPersonName;

    @NotNull
    @Email
    @Size(max = 120)
    private String contactEmail;

    @NotNull
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String contactPhone;

    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    private String status;
    private Boolean isDeleted;

    @NotNull
    private Long organizationId;

    private BankAccountRequestDTO bankAccount;
}
