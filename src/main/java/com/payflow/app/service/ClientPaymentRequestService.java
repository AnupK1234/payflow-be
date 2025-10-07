package com.payflow.app.service;

import com.payflow.app.dto.request.ClientPaymentRequestDTO;
import com.payflow.app.entity.ClientPaymentRequest;
import com.payflow.app.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface ClientPaymentRequestService {

    ClientPaymentRequest sendPaymentRequest(ClientPaymentRequestDTO requestDTO, Long orgId);

    List<ClientPaymentRequest> getPendingRequestsForClient(Long clientId);

    ClientPaymentRequest acceptPaymentRequest(Long requestId, Long clientBankAccountId);

    List<ClientPaymentRequest> getPaymentHistoryForClient(Long clientId,
                                                          LocalDateTime startDate,
                                                          LocalDateTime endDate,
                                                          PaymentStatus status);
}
