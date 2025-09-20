package com.payflow.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.payflow.app.dto.request.CreateOrganizationRequest;
import com.payflow.app.dto.request.UpdateOrganizationRequest;
import com.payflow.app.dto.response.OrganizationResponse;
import com.payflow.app.entity.Organization;
import com.payflow.app.entity.User;
import com.payflow.app.enums.Role;
import com.payflow.app.enums.Status;
import com.payflow.app.exception.NotFoundException;
import com.payflow.app.repository.OrganizationRepository;
import com.payflow.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

	private final OrganizationRepository organizationRepository;
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder encoder;
	private final ModelMapper modelMapper;

	private OrganizationResponse toResponse(Organization org) {
		OrganizationResponse res = modelMapper.map(org, OrganizationResponse.class);
		res.setStatus(org.getStatus().name()); 
		return res;
	}

	@Override
	public OrganizationResponse registerOrganization(CreateOrganizationRequest req) {
		Organization org = Organization.builder().name(req.getName()).registrationNumber(req.getRegistrationNumber())
				.address(req.getAddress()).status(Status.PENDING).build();
		org = organizationRepository.save(org);

		User admin = User.builder().username(req.getAdminUsername()).email(req.getAdminEmail())
				.passwordHash(encoder.encode(req.getTempPassword())).role(Role.ORG_ADMIN).organization(org)
				.mustResetPassword(true).enabled(true).build();
		userRepository.save(admin);
		org.setAdminUser(admin);

		return toResponse(organizationRepository.save(org));
	}

	@Override
	public List<OrganizationResponse> listAll() {
		return organizationRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
	}

	@Override
	public OrganizationResponse getById(Long id) {
		Organization org = organizationRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Organization not found with id: " + id));
		return toResponse(org);
	}

	@Override
	public OrganizationResponse updateOrganization(Long id, UpdateOrganizationRequest req) {
		Organization org = organizationRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Organization not found with id: " + id));

		if (req.getName() != null)
			org.setName(req.getName());
		if (req.getRegistrationNumber() != null)
			org.setRegistrationNumber(req.getRegistrationNumber());
		if (req.getAddress() != null)
			org.setAddress(req.getAddress());

		return toResponse(organizationRepository.save(org));
	}

	@Override
	public void deleteOrganization(Long id) {
		Organization org = organizationRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Organization not found with id: " + id));
		organizationRepository.delete(org);
	}

	@Override
	public OrganizationResponse verifyOrganization(Long id, boolean approve) {
		Organization org = organizationRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Organization not found with id: " + id));

		org.setStatus(approve ? Status.VERIFIED : Status.REJECTED);
		return toResponse(organizationRepository.save(org));
	}
}
