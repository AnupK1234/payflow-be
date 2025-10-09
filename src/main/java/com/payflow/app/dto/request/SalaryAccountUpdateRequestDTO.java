//package com.payflow.app.dto.request;
//
//import jakarta.validation.constraints.NotBlank;
//import lombok.Data;
//
//@Data
//public class SalaryAccountUpdateRequestDTO {
//
//    @NotBlank(message = "Bank name is required")
//    private String bankName;
//
//    @NotBlank(message = "Account number is required")
//    private String accountNumber;
//
//    @NotBlank(message = "IFSC code is required")
//    private String ifscCode;
//
//    private String additionalInfo; // optional
//}


package com.payflow.app.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SalaryAccountUpdateRequestDTO {

    @NotBlank(message = "Bank name is required")
    private String bankName;

    @NotBlank(message = "Account number is required")
    @Size(min = 9, max = 18, message = "Account number must be between 9 and 18 digits")
    @Pattern(regexp = "^[0-9]+$", message = "Account number must contain only digits")
    private String accountNumber;

    @NotBlank(message = "IFSC code is required")
    @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC code format")
    private String ifscCode;

    private String additionalInfo; // optional
}
