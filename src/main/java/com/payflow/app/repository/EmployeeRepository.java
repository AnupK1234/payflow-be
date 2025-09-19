package com.payflow.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payflow.app.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}