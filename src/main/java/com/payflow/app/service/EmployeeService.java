package com.payflow.app.service;

import java.time.LocalDate;
import java.util.List;

import com.payflow.app.dto.request.CreateEmployeeRequestDTO;
import com.payflow.app.dto.request.EmployeeSalaryStructureRequestDTO;
import com.payflow.app.dto.request.UpdateEmployeeRequestDTO;
import com.payflow.app.dto.response.EmployeeResponseDTO;
import com.payflow.app.dto.response.EmployeeSalaryStructureResponseDTO;
import com.payflow.app.dto.response.SalaryAccountUpdateRequestResponseDTO;

import jakarta.servlet.http.HttpServletRequest;

public interface EmployeeService {

	EmployeeResponseDTO createEmployee(CreateEmployeeRequestDTO req, HttpServletRequest request);

	List<EmployeeResponseDTO> getAllEmployees();

	EmployeeResponseDTO getEmployeeById(Long id);

	EmployeeResponseDTO updateEmployee(Long id, UpdateEmployeeRequestDTO req);

	void deleteEmployee(Long id);

	EmployeeSalaryStructureResponseDTO addSalaryStructure(Long employeeId, EmployeeSalaryStructureRequestDTO req);

	List<EmployeeSalaryStructureResponseDTO> getSalaryStructures(Long employeeId);

	List<SalaryAccountUpdateRequestResponseDTO> getPendingSalaryAccountRequests(Long orgId);

	boolean processSalaryAccountRequest(Long requestId, boolean approve);

	boolean processSalaryAccountRequest(Long requestId, boolean approve, Long adminId);

	List<SalaryAccountUpdateRequestResponseDTO> getSalaryAccountRequestsWithFilters(String employeeName,
			String bankName, String status, LocalDate startDate, LocalDate endDate);

	List<SalaryAccountUpdateRequestResponseDTO> getRequestsByOrganizationId(HttpServletRequest request);

}
