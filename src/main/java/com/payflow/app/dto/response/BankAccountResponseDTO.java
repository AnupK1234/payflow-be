package com.payflow.app.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccountResponseDTO {

    private String accountNumber; 
    private String ifsc;
    private String status;
}
