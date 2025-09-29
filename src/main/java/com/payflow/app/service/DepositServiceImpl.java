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
import org.springframework.transaction.annotation.Transactional;

import com.payflow.app.dto.request.CreateDepositRequest;
import com.payflow.app.dto.response.DepositResponse;
import com.payflow.app.entity.BankAccount;
import com.payflow.app.entity.Client;
import com.payflow.app.entity.DepositRequest;
import com.payflow.app.entity.Organization;
import com.payflow.app.entity.User;
import com.payflow.app.enums.DepositStatus;
import com.payflow.app.enums.Role;
import com.payflow.app.exception.NotFoundException;
import com.payflow.app.repository.BankAccountRepository;
import com.payflow.app.repository.ClientRepository;
import com.payflow.app.repository.DepositRequestRepository;
import com.payflow.app.repository.OrganizationRepository;
import com.payflow.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DepositServiceImpl implements DepositService {

    private final DepositRequestRepository depositRepo;
    private final OrganizationRepository orgRepo;
    private final ClientRepository clientRepo;
    private final UserRepository userRepo;
    private final BankAccountRepository bankAccountRepo;
    private final ModelMapper mapper;

    // ---------------- Create Deposit for Org ----------------
    @Override
    public DepositResponse createDepositForOrg(Long orgId, CreateDepositRequest req) {
        Organization org = orgRepo.findById(orgId)
                .orElseThrow(() -> new NotFoundException("Organization not found"));

        User loggedUser = getLoggedUser();

        DepositRequest deposit = DepositRequest.builder()
                .organization(org)
                .client(null)
                .createdBy(loggedUser)
                .amount(req.getAmount())
                .status(DepositStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        deposit = depositRepo.save(deposit);
        return toResponse(deposit);
    }

    // ---------------- Create Deposit for Client ----------------
    @Override
    public DepositResponse createDepositForClient(Long clientId, CreateDepositRequest req) {
        Client client = clientRepo.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client not found"));

        User loggedUser = getLoggedUser();

        DepositRequest deposit = DepositRequest.builder()
                .client(client)
                .organization(null)
                .createdBy(loggedUser)
                .amount(req.getAmount())
                .status(DepositStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        deposit = depositRepo.save(deposit);
        return toResponse(deposit);
    }

    // ---------------- Approve/Reject Deposit ----------------
    @Override
    public DepositResponse approveDeposit(Long depositId, boolean approve) {
        DepositRequest deposit = depositRepo.findById(depositId)
                .orElseThrow(() -> new NotFoundException("Deposit request not found"));

        User bankAdmin = getLoggedUser();
        deposit.setApprovedBy(bankAdmin);
        deposit.setApprovedAt(LocalDateTime.now());

        if (approve) {
            deposit.setStatus(DepositStatus.APPROVED);

            BankAccount account;
            if (deposit.getOrganization() != null) {
                account = bankAccountRepo.findByOrganizationAndOwnerTypeAndStatus(deposit.getOrganization(), Role.ORG_ADMIN, "ACTIVE")
                        .orElseThrow(() -> new NotFoundException("Active org bank account not found"));
            } else if (deposit.getClient() != null) {
                account = bankAccountRepo.findByClientId(deposit.getClient().getId())
                        .orElseThrow(() -> new NotFoundException("Active client bank account not found"));
            } else {
                throw new NotFoundException("No account linked to deposit");
            }

            account.setBalance(account.getBalance() + deposit.getAmount());
            bankAccountRepo.save(account);

        } else {
            deposit.setStatus(DepositStatus.REJECTED);
        }

        return toResponse(depositRepo.save(deposit));
    }

    // ---------------- List by Org ----------------
    @Override
    public List<DepositResponse> listByOrg(Long orgId) {
        return depositRepo.findByOrganizationId(orgId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ---------------- List by Client ----------------
    @Override
    public List<DepositResponse> listByClient(Long clientId) {
        return depositRepo.findByClientId(clientId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ---------------- List for Bank ----------------
    @Override
    public Page<DepositResponse> listForBank(DepositStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable) {
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

    // ---------------- List for Logged-in User ----------------
    @Override
    public List<DepositResponse> listDepositsForLoggedInUser() {
        User user = getLoggedUser();

        if (user.getRole() == Role.ORG_ADMIN) {
            return listByOrg(user.getOrganization().getId());
        } else if (user.getRole() == Role.CLIENT) {
            return listByClient(user.getClient().getId());
        } else {
            throw new RuntimeException("Unauthorized role");
        }
    }

    // ---------------- Helpers ----------------
    private DepositResponse toResponse(DepositRequest deposit) {
        DepositResponse res = mapper.map(deposit, DepositResponse.class);
        res.setStatus(deposit.getStatus().name());
        if (deposit.getCreatedBy() != null) res.setCreatedBy(deposit.getCreatedBy().getUsername());
        if (deposit.getApprovedBy() != null) res.setApprovedBy(deposit.getApprovedBy().getUsername());
        return res;
    }

    private User getLoggedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepo.findByUsername(auth.getName())
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public Long getOrgIdForUsername(String username) {
        User user = userRepo.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
        return user.getOrganization() != null ? user.getOrganization().getId() : null;
    }

    @Override
    public Long getClientIdForUsername(String username) {
        User user = userRepo.findByUsername(username).orElseThrow(() -> new NotFoundException("User not found"));
        return user.getClient() != null ? user.getClient().getId() : null;
    }
}
