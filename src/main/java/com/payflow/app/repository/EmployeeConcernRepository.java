package com.payflow.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.payflow.app.entity.EmployeeConcern;

public interface EmployeeConcernRepository extends JpaRepository<EmployeeConcern, Long> {
	List<EmployeeConcern> findByOrganizationId(Long organizationId);

	List<EmployeeConcern> findByEmployeeId(Long employeeId);
	
	@Query("SELECT ec.employee.organization.id FROM EmployeeConcern ec WHERE ec.employee.id = :employeeId")
    Long findOrganizationIdByEmployeeId(@Param("employeeId") Long employeeId);
}
