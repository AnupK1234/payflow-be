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

    
    Optional<BankAccount> findByClientId(Long clientId);

   
    Optional<BankAccount> findByClientAndOwnerTypeAndStatus(Client client, Role ownerType, String status);

   
    List<BankAccount> findAllByClientIdAndOwnerTypeAndStatusIgnoreCase(Long clientId, Role ownerType, String status);

    
    Optional<BankAccount> findByOrganizationId(Long organizationId);

    Optional<BankAccount> findByOrganizationAndStatus(Organization organization, String status);

    Optional<BankAccount> findByOrganizationAndOwnerTypeAndStatus(Organization organization, Role ownerType, String status);

   
    List<BankAccount> findByEmployeeId(Long employeeId);

    Optional<BankAccount> findByEmployeeAndOwnerTypeAndStatus(Employee employee, Role ownerType, String status);

}
