package com.payflow.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payflow.app.entity.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    // Get all active clients in a specific organization
    List<Client> findByOrganizationIdAndIsDeletedFalse(Long organizationId);

    // Get a specific active client by ID within an organization
    Optional<Client> findByIdAndOrganizationIdAndIsDeletedFalse(Long id, Long organizationId);
}
