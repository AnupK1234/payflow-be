package com.payflow.app.service;

import com.payflow.app.dto.request.ClientPaymentRequestDTO;
import com.payflow.app.entity.ClientPaymentRequest;

import java.util.List;

public interface ClientPaymentRequestService {

    ClientPaymentRequest sendPaymentRequest(ClientPaymentRequestDTO requestDTO);

    List<ClientPaymentRequest> getPendingRequestsForClient(Long clientId);

    ClientPaymentRequest acceptPaymentRequest(Long requestId, Long clientBankAccountId);
}
