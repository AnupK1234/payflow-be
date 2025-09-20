package com.payflow.app.service;

import java.util.List;

import com.payflow.app.dto.request.CreateOrganizationRequest;
import com.payflow.app.dto.request.UpdateOrganizationRequest;
import com.payflow.app.dto.response.OrganizationResponse;

public interface OrganizationService {

	OrganizationResponse registerOrganization(CreateOrganizationRequest req);

	List<OrganizationResponse> listAll();

	OrganizationResponse getById(Long id);

	OrganizationResponse updateOrganization(Long id, UpdateOrganizationRequest req);

	void deleteOrganization(Long id);

	OrganizationResponse verifyOrganization(Long id, boolean approve);
}
