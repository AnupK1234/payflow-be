package com.payflow.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payflow.app.entity.BankAccount;
import com.payflow.app.entity.Client;
import com.payflow.app.entity.Employee;

import com.payflow.app.entity.Organization;
import com.payflow.app.enums.Role;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {

	// Fetch the bank account of a client
	Optional<BankAccount> findByClientId(Long clientId);

	// Fetch the bank account of an organization
	Optional<BankAccount> findByOrganizationId(Long organizationId);

	// Fetch organization bank account by organization and status
	Optional<BankAccount> findByOrganizationAndStatus(Organization organization, String status);


	List<BankAccount> findByEmployeeId(Long employeeId);

	Optional<BankAccount> findByOrganizationAndOwnerTypeAndStatus(Organization organization, Role ownerType,
			String status);

	// Employee account for a given employee
	Optional<BankAccount> findByEmployeeAndOwnerTypeAndStatus(Employee employee, Role ownerType, String status);

    // For client deposits
    Optional<BankAccount> findByClientAndOwnerTypeAndStatus(
            Client client,
            Role ownerType,
            String status
    );
}
