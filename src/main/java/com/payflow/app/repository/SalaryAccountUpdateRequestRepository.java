package com.payflow.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payflow.app.entity.Organization;
import com.payflow.app.entity.SalaryAccountUpdateRequest;

@Repository
public interface SalaryAccountUpdateRequestRepository extends JpaRepository<SalaryAccountUpdateRequest, Long> {

    // Fetch all requests by status and organization id
    List<SalaryAccountUpdateRequest> findByOrgIdAndStatus(Long orgId, String status);
    List<SalaryAccountUpdateRequest> findByEmployee_Organization_IdAndStatus(Long orgId, String status);
    List<SalaryAccountUpdateRequest> findByOrg(Organization org);
}
