package com.payflow.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payflow.app.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
	List<Employee> findByOrganizationIdAndIsDeletedFalse(Long organizationId);
	List<Employee> findByIsDeletedFalse();

}