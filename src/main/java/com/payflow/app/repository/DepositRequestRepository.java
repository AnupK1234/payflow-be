package com.payflow.app.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.payflow.app.entity.DepositRequest;

public interface DepositRequestRepository extends JpaRepository<DepositRequest, Long> {
    List<DepositRequest> findByOrganizationId(Long orgId);
}
