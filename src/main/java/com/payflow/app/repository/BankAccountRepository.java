package com.payflow.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payflow.app.entity.BankAccount;
import com.payflow.app.entity.Organization;
import com.payflow.app.enums.Role;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    Optional<BankAccount> findByOrganizationAndOwnerTypeAndStatus(
            Organization organization,
            Role ownerType,
            String status
    );
}
