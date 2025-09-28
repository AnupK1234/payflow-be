package com.payflow.app.service;

import com.payflow.app.dto.request.ClientPaymentRequestDTO;
import com.payflow.app.entity.BankAccount;
import com.payflow.app.entity.ClientPaymentRequest;
import com.payflow.app.entity.Client;
import com.payflow.app.entity.Organization;
import com.payflow.app.exception.InsufficientFundsException;
import com.payflow.app.exception.NotFoundException;
import com.payflow.app.repository.BankAccountRepository;
import com.payflow.app.repository.ClientPaymentRequestRepository;
import com.payflow.app.repository.ClientRepository;
import com.payflow.app.repository.OrganizationRepository;
import com.payflow.app.service.ClientPaymentRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientPaymentRequestServiceImpl implements ClientPaymentRequestService {

    private final ClientPaymentRequestRepository requestRepo;
    private final ClientRepository clientRepo;
    private final OrganizationRepository orgRepo;
    private final BankAccountRepository bankRepo;

    @Override
    public ClientPaymentRequest sendPaymentRequest(ClientPaymentRequestDTO dto) {
        Organization org = orgRepo.findById(dto.getOrganizationId())
                .orElseThrow(() -> new NotFoundException("Organization not found"));

        Client client = clientRepo.findById(dto.getClientId())
                .orElseThrow(() -> new NotFoundException("Client not found"));

        ClientPaymentRequest request = ClientPaymentRequest.builder()
                .organization(org)
                .client(client)
                .amount(dto.getAmount())
                .reason(dto.getReason())
                .metadata(dto.getMetadata())
                .status("PENDING")
                .build();

        return requestRepo.save(request);
    }

    @Override
    public List<ClientPaymentRequest> getPendingRequestsForClient(Long clientId) {
        return requestRepo.findByClientIdAndStatus(clientId, "PENDING");
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

        // Debit client
        clientAcc.setBalance(clientAcc.getBalance() - req.getAmount());
        bankRepo.save(clientAcc);

        // Credit organization
        orgAcc.setBalance(orgAcc.getBalance() + req.getAmount());
        bankRepo.save(orgAcc);

        // Update request status
        req.setStatus("ACCEPTED");
        req.setAcceptedAt(LocalDateTime.now());
        return requestRepo.save(req);
    }
}
