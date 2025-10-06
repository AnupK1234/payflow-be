package com.payflow.app.repository;

import com.payflow.app.entity.ClientPaymentRequest;
import com.payflow.app.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ClientPaymentRequestRepository extends JpaRepository<ClientPaymentRequest, Long> {

    List<ClientPaymentRequest> findByClientId(Long clientId);

    List<ClientPaymentRequest> findByClientIdAndStatus(Long clientId, PaymentStatus status);

    List<ClientPaymentRequest> findByClientIdAndCreatedAtBetween(Long clientId, LocalDateTime startDate, LocalDateTime endDate);

    List<ClientPaymentRequest> findByClientIdAndStatusAndCreatedAtBetween(Long clientId, PaymentStatus status, LocalDateTime startDate, LocalDateTime endDate);

    List<ClientPaymentRequest> findByClientIdAndCreatedAtAfter(Long clientId, LocalDateTime startDate);

    List<ClientPaymentRequest> findByClientIdAndCreatedAtBefore(Long clientId, LocalDateTime endDate);
}
