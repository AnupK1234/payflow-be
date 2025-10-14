package com.payflow.app.service;

import com.payflow.app.dto.request.ClientPaymentRequestDTO;
import com.payflow.app.dto.response.UserResponse;
import com.payflow.app.entity.BankAccount;
import com.payflow.app.entity.ClientPaymentRequest;
import com.payflow.app.entity.Client;
import com.payflow.app.entity.Organization;
import com.payflow.app.enums.PaymentStatus;
import com.payflow.app.enums.Role;
import com.payflow.app.exception.InsufficientFundsException;
import com.payflow.app.exception.NotFoundException;
import com.payflow.app.repository.BankAccountRepository;
import com.payflow.app.repository.ClientPaymentRequestRepository;
import com.payflow.app.repository.ClientRepository;
import com.payflow.app.repository.OrganizationRepository;
import com.payflow.app.security.util.CurrentUserUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service("clientPaymentRequestService")
@RequiredArgsConstructor
public class ClientPaymentRequestServiceImpl implements ClientPaymentRequestService {

    private final ClientPaymentRequestRepository requestRepo;
    private final ClientRepository clientRepo;
    private final OrganizationRepository orgRepo;
    private final BankAccountRepository bankRepo;
    private final CurrentUserUtil currentUserUtil;

    
    @Override
    public ClientPaymentRequest sendPaymentRequest(ClientPaymentRequestDTO dto, HttpServletRequest request) {
        UserResponse currentUser = currentUserUtil.getCurrentUser(request);
        Long orgId = currentUser.getOrganizationId();

        Organization org = orgRepo.findById(orgId)
                .orElseThrow(() -> new NotFoundException("Organization not found"));

        Client client = clientRepo.findById(dto.getClientId())
                .orElseThrow(() -> new NotFoundException("Client not found"));

        ClientPaymentRequest paymentRequest = ClientPaymentRequest.builder()
                .organization(org)
                .client(client)
                .amount(dto.getAmount())
                .reason(dto.getReason())
                .metadata(dto.getMetadata())
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return requestRepo.save(paymentRequest);
    }

    
    @Override
    public List<ClientPaymentRequest> getPendingRequestsForClient(HttpServletRequest request) {
        UserResponse currentUser = currentUserUtil.getCurrentUser(request);
        Role role = Role.valueOf(currentUser.getRole());

        if (role != Role.CLIENT) {
            throw new RuntimeException("Only clients can view pending requests");
        }

        Long clientId = currentUser.getClientId(); // ✅ use clientId from token
        return requestRepo.findByClientIdAndStatus(clientId, PaymentStatus.PENDING);
    }

    
    @Override
    @Transactional
    public ClientPaymentRequest acceptPaymentRequest(Long requestId, Long clientBankAccountId) {
        ClientPaymentRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Payment request not found"));

        BankAccount clientAcc = bankRepo.findById(clientBankAccountId)
                .orElseThrow(() -> new NotFoundException("Client bank account not found"));

        BankAccount orgAcc = bankRepo.findByOrganizationId(req.getOrganization().getId())
                .orElseThrow(() -> new NotFoundException("Organization bank account not found"));

        if (clientAcc.getBalance() < req.getAmount()) {
            throw new InsufficientFundsException("Client has insufficient funds");
        }

        clientAcc.setBalance(clientAcc.getBalance() - req.getAmount());
        orgAcc.setBalance(orgAcc.getBalance() + req.getAmount());

        bankRepo.save(clientAcc);
        bankRepo.save(orgAcc);

        req.setStatus(PaymentStatus.ACCEPTED);
        req.setAcceptedAt(LocalDateTime.now());

        return requestRepo.save(req);
    }

   
    @Override
    @Transactional
    public ClientPaymentRequest rejectPaymentRequest(Long requestId) {
        ClientPaymentRequest req = requestRepo.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Payment request not found"));

        req.setStatus(PaymentStatus.REJECTED);
        req.setRejectedAt(LocalDateTime.now());

        return requestRepo.save(req);
    }

    
    @Override
    public List<ClientPaymentRequest> getPaymentHistoryForCurrentUser(HttpServletRequest request,
                                                                      LocalDateTime startDate,
                                                                      LocalDateTime endDate,
                                                                      PaymentStatus status) {
        UserResponse currentUser = currentUserUtil.getCurrentUser(request);
        Role role = Role.valueOf(currentUser.getRole());
        List<ClientPaymentRequest> result;

        if (role == Role.CLIENT) {
            Long clientId = currentUser.getClientId();
            result = fetchClientHistory(clientId, startDate, endDate, status);
        } else if (role == Role.ORG_ADMIN) {
            Long orgId = currentUser.getOrganizationId();
            result = fetchOrgHistory(orgId, startDate, endDate, status);
        } else {
            throw new RuntimeException("Unauthorized role for viewing history");
        }

        return result;
    }

    
    private List<ClientPaymentRequest> fetchClientHistory(Long clientId, LocalDateTime startDate,
                                                          LocalDateTime endDate, PaymentStatus status) {
        if (startDate != null && endDate != null && status != null) {
            return requestRepo.findByClientIdAndStatusAndCreatedAtBetween(clientId, status, startDate, endDate);
        } else if (startDate != null && endDate != null) {
            return requestRepo.findByClientIdAndCreatedAtBetween(clientId, startDate, endDate);
        } else if (startDate != null) {
            return requestRepo.findByClientIdAndCreatedAtAfter(clientId, startDate);
        } else if (endDate != null) {
            return requestRepo.findByClientIdAndCreatedAtBefore(clientId, endDate);
        } else if (status != null) {
            return requestRepo.findByClientIdAndStatus(clientId, status);
        } else {
            return requestRepo.findByClientId(clientId);
        }
    }

    private List<ClientPaymentRequest> fetchOrgHistory(Long orgId, LocalDateTime startDate,
                                                       LocalDateTime endDate, PaymentStatus status) {
        if (startDate != null && endDate != null && status != null) {
            return requestRepo.findByOrganizationIdAndStatusAndCreatedAtBetween(orgId, status, startDate, endDate);
        } else if (startDate != null && endDate != null) {
            return requestRepo.findByOrganizationIdAndCreatedAtBetween(orgId, startDate, endDate);
        } else if (startDate != null) {
            return requestRepo.findByOrganizationIdAndCreatedAtAfter(orgId, startDate);
        } else if (endDate != null) {
            return requestRepo.findByOrganizationIdAndCreatedAtBefore(orgId, endDate);
        } else if (status != null) {
            return requestRepo.findByOrganizationIdAndStatus(orgId, status);
        } else {
            return requestRepo.findByOrganizationId(orgId);
        }
    }

 
    public List<BankAccount> getClientBankAccounts(HttpServletRequest request) {
        UserResponse currentUser = currentUserUtil.getCurrentUser(request);
        Long clientId = currentUser.getClientId(); 
        return bankRepo.findAllByClientIdAndOwnerTypeAndStatusIgnoreCase(clientId, Role.CLIENT, "ACTIVE");
    }
}
