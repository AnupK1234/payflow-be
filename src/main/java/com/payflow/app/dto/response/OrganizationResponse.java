package com.payflow.app.dto.response;

import lombok.Data;

@Data
public class OrganizationResponse {
	private Long id;
	private String name;
	private String registrationNumber;
	private String address;
	private String status;
	private Boolean isActive;
}
