package com.payflow.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payflow.app.entity.BankAccount;
import com.payflow.app.entity.Organization;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

    // Find the ACTIVE bank account for a given organization
    Optional<BankAccount> findByOrganizationAndStatus(Organization organization, String status);
}
