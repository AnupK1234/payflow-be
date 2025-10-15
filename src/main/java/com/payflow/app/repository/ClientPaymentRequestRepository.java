package com.payflow.app.repository;

import com.payflow.app.entity.ClientPaymentRequest;
import com.payflow.app.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ClientPaymentRequestRepository extends JpaRepository<ClientPaymentRequest, Long> {

    // ------------------- Client queries -------------------
    
    // Get all requests for a client
    List<ClientPaymentRequest> findByClientId(Long clientId);

    // Get all requests for a client by status
    List<ClientPaymentRequest> findByClientIdAndStatus(Long clientId, PaymentStatus status);

    // Get all requests for a client within a date range
    List<ClientPaymentRequest> findByClientIdAndCreatedAtBetween(Long clientId, LocalDateTime startDate, LocalDateTime endDate);

    // Get all requests for a client by status and date range
    List<ClientPaymentRequest> findByClientIdAndStatusAndCreatedAtBetween(Long clientId, PaymentStatus status, LocalDateTime startDate, LocalDateTime endDate);

    // Get all requests for a client after a certain date
    List<ClientPaymentRequest> findByClientIdAndCreatedAtAfter(Long clientId, LocalDateTime startDate);

    // Get all requests for a client before a certain date
    List<ClientPaymentRequest> findByClientIdAndCreatedAtBefore(Long clientId, LocalDateTime endDate);

    // ------------------- Organization queries -------------------

    // Get all requests for an organization
    List<ClientPaymentRequest> findByOrganizationId(Long orgId);

    // Get all requests for an organization by status
    List<ClientPaymentRequest> findByOrganizationIdAndStatus(Long orgId, PaymentStatus status);

    // Get all requests for an organization within a date range
    List<ClientPaymentRequest> findByOrganizationIdAndCreatedAtBetween(Long orgId, LocalDateTime startDate, LocalDateTime endDate);

    // Get all requests for an organization by status and date range
    List<ClientPaymentRequest> findByOrganizationIdAndStatusAndCreatedAtBetween(Long orgId, PaymentStatus status, LocalDateTime startDate, LocalDateTime endDate);

    // Get all requests for an organization after a certain date
    List<ClientPaymentRequest> findByOrganizationIdAndCreatedAtAfter(Long orgId, LocalDateTime startDate);

    // Get all requests for an organization before a certain date
    List<ClientPaymentRequest> findByOrganizationIdAndCreatedAtBefore(Long orgId, LocalDateTime endDate);
}
