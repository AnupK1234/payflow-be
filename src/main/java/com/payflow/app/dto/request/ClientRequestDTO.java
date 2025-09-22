package com.payflow.app.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientRequestDTO {

    @NotBlank
    private String companyName;            

    @NotBlank
    private String contactPersonName;       

    @NotBlank
    @Email
    private String contactEmail;

    @NotBlank
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    private String contactPhone;

    private String address;
    private String city;
    private String state;
    private String country;
    private String postalCode;

    private String status;                  

    @NotNull
    private Long organizationId;            
}
