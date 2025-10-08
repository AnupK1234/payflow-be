package com.payflow.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.payflow.app.dto.request.OrganizationRequest;
import com.payflow.app.dto.response.OrganizationResponse;
import com.payflow.app.entity.Organization;
import com.payflow.app.enums.Status;
import com.payflow.app.exception.NotFoundException;
import com.payflow.app.repository.OrganizationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BankAdminServiceImpl implements BankAdminService {

	private final OrganizationRepository organizationRepository;
	private final ModelMapper modelMapper;

	private OrganizationResponse toResponse(Organization org) {
		OrganizationResponse dto = modelMapper.map(org, OrganizationResponse.class);

		// Convert enum to string
		dto.setStatus(org.getStatus().name());

		// Set admin info if present
		if (org.getAdminUser() != null) {
			dto.setAdminUsername(org.getAdminUser().getUsername());
			dto.setAdminEmail(org.getAdminUser().getEmail());
		}
		return dto;
	}

	@Override
	public List<OrganizationResponse> listPendingOrganizations() {
		return organizationRepository.findAll().stream().filter(org -> org.getStatus() == Status.PENDING)
				.map(this::toResponse).collect(Collectors.toList());
	}

	@Override
	public OrganizationResponse verifyOrganization(OrganizationRequest request) {
		Organization org = organizationRepository.findById(request.getOrganizationId()).orElseThrow(
				() -> new NotFoundException("Organization not found with id: " + request.getOrganizationId()));

		org.setStatus(request.isApprove() ? Status.VERIFIED : Status.REJECTED);
		return toResponse(organizationRepository.save(org));
	}

	@Override
	public OrganizationResponse getOrganizationDetails(Long organizationId) {
		Organization org = organizationRepository.findById(organizationId)
				.orElseThrow(() -> new NotFoundException("Organization not found with id: " + organizationId));
		return toResponse(org);
	}

	@Override
	public List<OrganizationResponse> listAllOrganizations() {
		return organizationRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
	}

	@Override
	public OrganizationResponse updateOrganizationStatus(Long organizationId, Status newStatus) {
		System.out.println("org is :" + organizationId);
		Organization org = organizationRepository.findById(organizationId)
				.orElseThrow(() -> new NotFoundException("Organization not found with ID: " + organizationId));

		// Add validation rules if needed
		if (newStatus == Status.SUSPENDED && org.getStatus() == Status.SUSPENDED) {
			throw new IllegalStateException("Organization is already suspended.");
		}
		if (newStatus == Status.VERIFIED && org.getStatus() == Status.VERIFIED) {
			throw new IllegalStateException("Organization is already active.");
		}

		org.setStatus(newStatus);
		Organization updated = organizationRepository.save(org);
		return toResponse(updated);
	}

}
