package com.payflow.app.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccountResponseDTO {

    private String accountNumber;  // decrypted if needed
    private String ifsc;
    private String status;
}
