//package com.payflow.app.service;
//
//import java.util.List;
//
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import com.payflow.app.dto.request.CreateOrganizationRequest;
//import com.payflow.app.dto.request.UpdateOrganizationRequest;
//import com.payflow.app.entity.Organization;
//import com.payflow.app.entity.User;
//import com.payflow.app.enums.Status;
//import com.payflow.app.exception.ApiException;
//import com.payflow.app.repository.OrganizationRepository;
//import com.payflow.app.repository.UserRepository;
//
//@Service
//public class OrganizationService {
//	private final OrganizationRepository orgRepo;
//	private final UserRepository userRepo;
//	private final BCryptPasswordEncoder encoder;
//
//	public OrganizationService(OrganizationRepository orgRepo, UserRepository userRepo, BCryptPasswordEncoder encoder) {
//		this.orgRepo = orgRepo;
//		this.userRepo = userRepo;
//		this.encoder = encoder;
//	}
//
//	public List<Organization> listAll() {
//		return orgRepo.findAll();
//	}
//
//	public Organization getById(Long id) {
//		return orgRepo.findById(id).orElseThrow(() -> new ApiException.NotFound("Organization not found"));
//	}
//
//	public Organization registerOrganization(CreateOrganizationRequest req) {
//		Organization org = Organization.builder().name(req.getName()).registrationNumber(req.getRegistrationNumber())
//				.address(req.getAddress()).status(Status.PENDING).build();
//		org = orgRepo.save(org);
//
//		User admin = User.builder().username(req.getAdminUsername()).email(req.getAdminEmail())
//				.passwordHash(encoder.encode(req.getTempPassword())).role("ORG_ADMIN").organization(org)
//				.mustResetPassword(true).enabled(true).build();
//		userRepo.save(admin);
//		org.setAdminUser(admin);
//
//		return orgRepo.save(org);
//	}
//
//	public Organization updateOrganization(Long id, UpdateOrganizationRequest req) {
//		Organization org = getById(id);
//		if (req.getName() != null)
//			org.setName(req.getName());
//		if (req.getRegistrationNumber() != null)
//			org.setRegistrationNumber(req.getRegistrationNumber());
//		if (req.getAddress() != null)
//			org.setAddress(req.getAddress());
//		return orgRepo.save(org);
//	}
//
//	public void deleteOrganization(Long id) {
//		Organization org = getById(id);
//		orgRepo.delete(org);
//	}
//
//	public Organization verifyOrganization(Long id, boolean approve) {
//		Organization org = getById(id);
//		org.setStatus(approve ? Status.VERIFIED : Status.REJECTED);
//		return orgRepo.save(org);
//	}
//}
