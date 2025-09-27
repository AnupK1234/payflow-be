package com.payflow.app.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.payflow.app.dto.request.CreateDepositRequest;
import com.payflow.app.dto.response.DepositResponse;
import com.payflow.app.entity.BankAccount;
import com.payflow.app.entity.DepositRequest;
import com.payflow.app.entity.Organization;
import com.payflow.app.entity.User;
import com.payflow.app.enums.DepositStatus;
import com.payflow.app.enums.Role;
import com.payflow.app.exception.NotFoundException;
import com.payflow.app.repository.BankAccountRepository;
import com.payflow.app.repository.DepositRequestRepository;
import com.payflow.app.repository.OrganizationRepository;
import com.payflow.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DepositServiceImpl implements DepositService {

	private final DepositRequestRepository depositRepo;
	private final OrganizationRepository orgRepo;
	private final UserRepository userRepo;
	private final BankAccountRepository bankAccountRepo;
	private final ModelMapper mapper;

	@Override
	public DepositResponse createDeposit(Long orgId, CreateDepositRequest req) {
		// Get logged-in Org Admin
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User orgAdmin = userRepo.findByUsername(auth.getName())
				.orElseThrow(() -> new NotFoundException("Org admin not found"));

		Organization org = orgRepo.findById(orgId).orElseThrow(() -> new NotFoundException("Organization not found"));

		DepositRequest deposit = DepositRequest.builder().organization(org).createdBy(orgAdmin).amount(req.getAmount())
				.status(DepositStatus.PENDING).build();

		deposit = depositRepo.save(deposit);
		return toResponse(deposit);
	}

	@Override
	public DepositResponse approveDeposit(Long depositId, boolean approve) {
		DepositRequest deposit = depositRepo.findById(depositId)
				.orElseThrow(() -> new NotFoundException("Deposit request not found"));

		// Get logged-in Bank Admin
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User bankAdmin = userRepo.findByUsername(auth.getName())
				.orElseThrow(() -> new NotFoundException("Bank admin not found"));

		deposit.setApprovedBy(bankAdmin);
		deposit.setApprovedAt(LocalDateTime.now());

		if (approve) {
			deposit.setStatus(DepositStatus.APPROVED);

			BankAccount account = bankAccountRepo
					.findByOrganizationAndOwnerTypeAndStatus(deposit.getOrganization(), Role.ORG_ADMIN, "ACTIVE")
					.orElseThrow(() -> new NotFoundException("Active bank account not found"));

			account.setBalance(account.getBalance() + deposit.getAmount());
			bankAccountRepo.save(account);
		} else {
			deposit.setStatus(DepositStatus.REJECTED);
		}

		return toResponse(depositRepo.save(deposit));
	}

	@Override
	public List<DepositResponse> listByOrg(Long orgId) {
		return depositRepo.findByOrganizationId(orgId).stream().map(this::toResponse).collect(Collectors.toList());
	}

	@Override
	public Page<DepositResponse> listForBank(DepositStatus status, LocalDate startDate, LocalDate endDate,
			Pageable pageable) {

		Page<DepositRequest> deposits;

		if (status != null && startDate != null && endDate != null) {
			deposits = depositRepo.findByStatusAndCreatedAtBetween(status, startDate.atStartOfDay(),
					endDate.atTime(23, 59, 59), pageable);
		} else if (status != null && startDate != null) {
			deposits = depositRepo.findByStatusAndCreatedAtAfter(status, startDate.atStartOfDay(), pageable);
		} else if (status != null && endDate != null) {
			deposits = depositRepo.findByStatusAndCreatedAtBefore(status, endDate.atTime(23, 59, 59), pageable);
		} else if (status != null) {
			deposits = depositRepo.findByStatus(status, pageable);
		} else if (startDate != null && endDate != null) {
			deposits = depositRepo.findByCreatedAtBetween(startDate.atStartOfDay(), endDate.atTime(23, 59, 59),
					pageable);
		} else if (startDate != null) {
			deposits = depositRepo.findByCreatedAtAfter(startDate.atStartOfDay(), pageable);
		} else if (endDate != null) {
			deposits = depositRepo.findByCreatedAtBefore(endDate.atTime(23, 59, 59), pageable);
		} else {
			deposits = depositRepo.findAll(pageable);
		}

		return deposits.map(this::toResponse);
	}

	private DepositResponse toResponse(DepositRequest deposit) {
		DepositResponse res = mapper.map(deposit, DepositResponse.class);
		res.setStatus(deposit.getStatus().name());
		if (deposit.getCreatedBy() != null)
			res.setCreatedBy(deposit.getCreatedBy().getUsername());
		if (deposit.getApprovedBy() != null)
			res.setApprovedBy(deposit.getApprovedBy().getUsername());
		return res;
	}
}
