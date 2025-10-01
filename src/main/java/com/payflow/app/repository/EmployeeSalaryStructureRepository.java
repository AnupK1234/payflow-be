package com.payflow.app.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.payflow.app.entity.EmployeeSalaryStructure;

import jakarta.transaction.Transactional;

public interface EmployeeSalaryStructureRepository extends JpaRepository<EmployeeSalaryStructure, Long> {

	// Existing method
	List<EmployeeSalaryStructure> findByEmployeeId(Long employeeId);

	// New method: fetch salary structures overlapping a date range
	@Query("SELECT s FROM EmployeeSalaryStructure s " + "WHERE s.employee.id = :employeeId "
			+ "AND s.effectiveFrom <= :endDate " + "AND s.effectiveTo >= :startDate")
	List<EmployeeSalaryStructure> findByEmployeeIdAndDateRange(@Param("employeeId") Long employeeId,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

	// Set all existing salary structures for an employee to inactive (isCurrent =
	// false)
	@Modifying
	@Transactional
	@Query("UPDATE EmployeeSalaryStructure s SET s.isCurrent = FALSE WHERE s.employee.id = :employeeId AND s.isCurrent = TRUE")
	void deactivateCurrentStructuresForEmployee(@Param("employeeId") Long employeeId);
}
