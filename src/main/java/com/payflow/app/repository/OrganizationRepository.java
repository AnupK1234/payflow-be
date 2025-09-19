package com.payflow.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payflow.app.entity.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
}