package com.payflow.app.repository;

import com.payflow.app.entity.ClientPaymentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClientPaymentRequestRepository extends JpaRepository<ClientPaymentRequest, Long> {

   
    List<ClientPaymentRequest> findByClientIdAndStatus(Long clientId, String status);
}
