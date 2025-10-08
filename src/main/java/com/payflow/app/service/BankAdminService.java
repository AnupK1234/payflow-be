package com.payflow.app.service;

import java.util.List;

import com.payflow.app.dto.request.OrganizationRequest;
import com.payflow.app.dto.response.OrganizationResponse;
import com.payflow.app.enums.Status;

public interface BankAdminService {

	List<OrganizationResponse> listPendingOrganizations();

	OrganizationResponse verifyOrganization(OrganizationRequest request);

	OrganizationResponse getOrganizationDetails(Long organizationId);

	List<OrganizationResponse> listAllOrganizations();

	OrganizationResponse updateOrganizationStatus(Long id, Status status);

	
}
