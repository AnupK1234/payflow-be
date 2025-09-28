package com.payflow.app.repository;

import com.payflow.app.entity.BankAccount;
import com.payflow.app.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    // Fetch the bank account of a client
    Optional<BankAccount> findByClientId(Long clientId);

    // Fetch the bank account of an organization
    Optional<BankAccount> findByOrganizationId(Long organizationId);

    // Fetch organization bank account by organization and status
    Optional<BankAccount> findByOrganizationAndStatus(Organization organization, String status);

    // ✅ Correct method name for employee
    List<BankAccount> findByEmployeeId(Long employeeId);
  
   Optional<BankAccount> findByOrganizationAndOwnerTypeAndStatus(
            Organization organization,
            Role ownerType,
            String status
    );

}
