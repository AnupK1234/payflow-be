package com.payflow.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payflow.app.entity.EmployeeConcern;

public interface EmployeeConcernRepository extends JpaRepository<EmployeeConcern, Long> {
	List<EmployeeConcern> findByOrganizationId(Long organizationId);

	List<EmployeeConcern> findByEmployeeId(Long employeeId);
}
