package com.payflow.app.repository;

import com.payflow.app.entity.EmployeeSalaryStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmployeeSalaryStructureRepository extends JpaRepository<EmployeeSalaryStructure, Long> {
    List<EmployeeSalaryStructure> findByEmployeeId(Long employeeId);
}
