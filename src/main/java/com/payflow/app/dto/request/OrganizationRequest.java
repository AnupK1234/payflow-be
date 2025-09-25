package com.payflow.app.dto.request;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationRequest {
	private Long organizationId;
	private boolean approve; // true = approve, false = reject
	
	 @Valid
	    private BankAccountRequestDTO bankAccount; 
}
