package com.payflow.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payflow.app.dto.request.EmployeeRequestDTO;
import com.payflow.app.dto.request.EmployeeSalaryStructureRequestDTO;
import com.payflow.app.dto.response.EmployeeResponseDTO;
import com.payflow.app.dto.response.EmployeeSalaryStructureResponseDTO;
import com.payflow.app.entity.Employee;
import com.payflow.app.entity.EmployeeSalaryStructure;
import com.payflow.app.entity.Organization;
import com.payflow.app.exception.NotFoundException;
import com.payflow.app.repository.EmployeeRepository;
import com.payflow.app.repository.EmployeeSalaryStructureRepository;
import com.payflow.app.repository.OrganizationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

	private final EmployeeRepository employeeRepository;
	private final EmployeeSalaryStructureRepository salaryStructureRepository;
	private final OrganizationRepository organizationRepository;
	private final ModelMapper modelMapper;

	@Override
	public EmployeeResponseDTO createEmployee(EmployeeRequestDTO req) {
		Employee employee = modelMapper.map(req, Employee.class);
		employee.setId(null); // Ensure Hibernate treats it as new
		Organization org = organizationRepository.findById(req.getOrganizationId())
				.orElseThrow(() -> new NotFoundException("Organization not found with id: " + req.getOrganizationId()));
		employee.setOrganization(org);
		employee = employeeRepository.save(employee);
		return modelMapper.map(employee, EmployeeResponseDTO.class);
	}

	@Override
	public List<EmployeeResponseDTO> getAllEmployees() {
		return employeeRepository.findAll().stream().map(emp -> modelMapper.map(emp, EmployeeResponseDTO.class))
				.collect(Collectors.toList());
	}

	@Override
	public EmployeeResponseDTO getEmployeeById(Long id) {
		Employee employee = employeeRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Employee not found with id: " + id));
		return modelMapper.map(employee, EmployeeResponseDTO.class);
	}

	@Override
	public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO req) {
		Employee employee = employeeRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Employee not found with id: " + id));

		// Update only fields, not collections
		employee.setFullName(req.getFullName());
		employee.setEmail(req.getEmail());
		employee.setEmployeeCode(req.getEmployeeCode());
		employee.setDateOfJoining(req.getDateOfJoining());
		employee.setJobTitle(req.getJobTitle());
		employee.setDepartment(req.getDepartment());
		employee.setStatus(req.getStatus());
		employee.setBankAccountNumber(req.getBankAccountNumber());
		employee.setIfscCode(req.getIfscCode());
		employee.setAadhaarNumber(req.getAadhaarNumber());
		employee.setPanNumber(req.getPanNumber());

		// Update organization if changed
		if (!employee.getOrganization().getId().equals(req.getOrganizationId())) {
			Organization org = organizationRepository.findById(req.getOrganizationId()).orElseThrow(
					() -> new NotFoundException("Organization not found with id: " + req.getOrganizationId()));
			employee.setOrganization(org);
		}

		employee = employeeRepository.save(employee);
		return modelMapper.map(employee, EmployeeResponseDTO.class);
	}

	@Override
	public void deleteEmployee(Long id) {
		Employee employee = employeeRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Employee not found with id: " + id));

		employee.setIsDeleted(true);
		employeeRepository.save(employee);
	}

	@Override
	public EmployeeSalaryStructureResponseDTO addSalaryStructure(Long employeeId,
			EmployeeSalaryStructureRequestDTO req) {
		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new NotFoundException("Employee not found with id: " + employeeId));

		EmployeeSalaryStructure structure = modelMapper.map(req, EmployeeSalaryStructure.class);
		structure.setEmployee(employee);

		structure = salaryStructureRepository.save(structure);
		return modelMapper.map(structure, EmployeeSalaryStructureResponseDTO.class);
	}

	@Override
	public List<EmployeeSalaryStructureResponseDTO> getSalaryStructures(Long employeeId) {
		if (!employeeRepository.existsById(employeeId)) {
			throw new NotFoundException("Employee not found with id: " + employeeId);
		}

		return salaryStructureRepository.findByEmployeeId(employeeId).stream()
				.map(struct -> modelMapper.map(struct, EmployeeSalaryStructureResponseDTO.class))
				.collect(Collectors.toList());
	}
}
