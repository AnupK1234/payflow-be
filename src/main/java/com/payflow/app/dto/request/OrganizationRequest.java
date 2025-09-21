package com.payflow.app.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationRequest {
	private Long organizationId;
	private boolean approve; // true = approve, false = reject
}
