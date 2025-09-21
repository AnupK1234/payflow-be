package com.payflow.app.service;

import java.util.List;

import com.payflow.app.dto.request.OrganizationRequest;
import com.payflow.app.dto.response.OrganizationResponse;

public interface BankAdminService {

	List<OrganizationResponse> listPendingOrganizations();

	OrganizationResponse verifyOrganization(OrganizationRequest request);

	OrganizationResponse getOrganizationDetails(Long organizationId);

	List<OrganizationResponse> listAllOrganizations();
}
