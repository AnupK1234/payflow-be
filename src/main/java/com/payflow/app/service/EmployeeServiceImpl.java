package com.payflow.app.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.payflow.app.dto.request.CreateEmployeeRequestDTO;
import com.payflow.app.dto.request.EmployeeSalaryStructureRequestDTO;
import com.payflow.app.dto.response.*;
import com.payflow.app.entity.*;
import com.payflow.app.enums.Role;
import com.payflow.app.exception.NotFoundException;
import com.payflow.app.repository.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeSalaryStructureRepository salaryStructureRepository;
    private final OrganizationRepository organizationRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final SalaryAccountUpdateRequestRepository salaryAccountUpdateRequestRepository;
    private final BankAccountRepository bankAccountRepository;

    // Employee CRUD 
    @Override

    public EmployeeResponseDTO createEmployee(CreateEmployeeRequestDTO req) {
        // 1️⃣ Map EmployeeRequestDTO to Employee
        Employee employee = modelMapper.map(req, Employee.class);
        employee.setId(null);

        Organization org = organizationRepository.findById(req.getOrganizationId())
                .orElseThrow(() -> new NotFoundException("Organization not found with id: " + req.getOrganizationId()));
        employee.setOrganization(org);

        if (req.getBankAccount() != null) {
            BankAccount bankAccount = BankAccount.builder()
                    .employee(employee)
                    .ownerType(Role.EMPLOYEE)
                    .accountNumberEnc(req.getBankAccount().getAccountNumber())
                    .ifsc(req.getBankAccount().getIfsc())
                    .status("ACTIVE")
                    .build();
            employee.setBankAccounts(List.of(bankAccount));
        }

        employee = employeeRepository.save(employee);

        User user = User.builder()
                .username(employee.getEmployeeCode())
                .email(employee.getEmail())
                .passwordHash(encoder.encode(employee.getEmployeeCode() + "123"))
                .role(Role.EMPLOYEE)
                .employee(employee)
                .mustResetPassword(true)
                .enabled(true)
                .build();

        userRepository.save(user);
        EmployeeResponseDTO response = modelMapper.map(employee, EmployeeResponseDTO.class);
        mapActiveBankAccountToResponse(employee, response);
        return response;
    }

    @Override
    public EmployeeResponseDTO updateEmployee(Long id, CreateEmployeeRequestDTO req) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found with id: " + id));

        modelMapper.map(req, employee);

        if (!employee.getOrganization().getId().equals(req.getOrganizationId())) {
            Organization org = organizationRepository.findById(req.getOrganizationId())
                    .orElseThrow(() -> new NotFoundException("Organization not found with id: " + req.getOrganizationId()));
            employee.setOrganization(org);
        }

        if (req.getBankAccount() != null && req.getBankAccount().getAccountNumber() != null) {
            employee.getBankAccounts().forEach(acc -> acc.setStatus("INACTIVE"));

            BankAccount bankAccount = BankAccount.builder()
                    .employee(employee)
                    .ownerType(Role.EMPLOYEE)
                    .accountNumberEnc(req.getBankAccount().getAccountNumber())
                    .ifsc(req.getBankAccount().getIfsc())
                    .status("ACTIVE")
                    .build();
            employee.getBankAccounts().add(bankAccount);
        }

        employee = employeeRepository.save(employee);
        EmployeeResponseDTO response = modelMapper.map(employee, EmployeeResponseDTO.class);
        mapActiveBankAccountToResponse(employee, response);
        return response;
    }

    @Override
    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(emp -> {
                    EmployeeResponseDTO dto = modelMapper.map(emp, EmployeeResponseDTO.class);
                    mapActiveBankAccountToResponse(emp, dto);
                    return dto;
                }).collect(Collectors.toList());
    }

    @Override
    public EmployeeResponseDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found with id: " + id));
        EmployeeResponseDTO dto = modelMapper.map(employee, EmployeeResponseDTO.class);
        mapActiveBankAccountToResponse(employee, dto);
        return dto;
    }

    @Override
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new NotFoundException("Employee not found with id: " + id);
        }
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found with id: " + id));

        employee.setIsDeleted(true);
        employee.setStatus("INACTIVE");
        employeeRepository.save(employee);
    }

    // Salary Structure 
    @Override
    public EmployeeSalaryStructureResponseDTO addSalaryStructure(Long employeeId,
            EmployeeSalaryStructureRequestDTO req) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Employee not found with id: " + employeeId));
        
        salaryStructureRepository.deactivateCurrentStructuresForEmployee(employeeId);

        EmployeeSalaryStructure structure = modelMapper.map(req, EmployeeSalaryStructure.class);
        structure.setEmployee(employee);
        structure.setIsCurrent(Boolean.TRUE); 

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

    //  Salary Account Requests 
    @Override
    public List<SalaryAccountUpdateRequestResponseDTO> getPendingSalaryAccountRequests(Long orgId) {
        List<SalaryAccountUpdateRequest> pendingRequests = salaryAccountUpdateRequestRepository
                .findByEmployee_Organization_IdAndStatus(orgId, "PENDING");

        return pendingRequests.stream()
                .map(req -> SalaryAccountUpdateRequestResponseDTO.builder()
                        .requestId(req.getId())
                        .employeeId(req.getEmployee().getId())
                        .employeeName(req.getEmployee().getFullName())
                        .bankName(req.getBankName())
                        .accountNumber(req.getAccountNumber())
                        .ifscCode(req.getIfscCode())
                        .status(req.getStatus())
                        .requestedAt(req.getRequestedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public boolean processSalaryAccountRequest(Long requestId, boolean approve) {
        SalaryAccountUpdateRequest request = salaryAccountUpdateRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Salary account update request not found"));

        if (!"PENDING".equals(request.getStatus())) {
            return false;
        }

        if (approve) {
            
            List<BankAccount> accounts = bankAccountRepository.findByEmployeeId(request.getEmployee().getId());
            accounts.forEach(acc -> acc.setStatus("INACTIVE"));
            bankAccountRepository.saveAll(accounts);

            
            BankAccount newAccount = BankAccount.builder()
                    .employee(request.getEmployee())
                    .ownerType(Role.EMPLOYEE)
                    .accountNumberEnc(request.getAccountNumber())
                    .ifsc(request.getIfscCode())
                    .status("ACTIVE")
                    .build();
            bankAccountRepository.save(newAccount);

            request.setStatus("APPROVED");
        } else {
            request.setStatus("REJECTED");
        }

        request.setProcessedAt(LocalDateTime.now());
        request.setApprovedBy(getCurrentAdminId());
        salaryAccountUpdateRequestRepository.save(request);

        return true;
    }

    // Helper Methods 
    private void mapActiveBankAccountToResponse(Employee employee, EmployeeResponseDTO response) {
        employee.getBankAccounts().stream()
                .filter(acc -> "ACTIVE".equals(acc.getStatus()))
                .findFirst()
                .ifPresent(ba -> response.setBankAccount(
                        BankAccountResponseDTO.builder()
                                .accountNumber(ba.getAccountNumberEnc())
                                .ifsc(ba.getIfsc())
                                .status(ba.getStatus())
                                .build()
                ));
    }

    private Long getCurrentAdminId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        com.payflow.app.entity.User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Logged-in admin not found"));

        return user.getId();
    }

	@Override
	public boolean processSalaryAccountRequest(Long requestId, boolean approve, Long adminId) {
		
		return false;
	}
	
	@Override
	public List<SalaryAccountUpdateRequestResponseDTO> getSalaryAccountRequestsWithFilters(
	        String employeeName, String bankName, String status,
	        LocalDate startDate, LocalDate endDate) {

	    List<SalaryAccountUpdateRequest> requests = salaryAccountUpdateRequestRepository.findAll();

	    return requests.stream()
	            
	            .filter(r -> employeeName == null || 
	                    r.getEmployee().getFullName().toLowerCase().contains(employeeName.toLowerCase()))
	            
	            .filter(r -> bankName == null || 
	                    r.getBankName().toLowerCase().contains(bankName.toLowerCase()))
	            
	            .filter(r -> status == null || r.getStatus().equalsIgnoreCase(status))
	            
	            .filter(r -> startDate == null || !r.getRequestedAt().toLocalDate().isBefore(startDate))
	            
	            .filter(r -> endDate == null || !r.getRequestedAt().toLocalDate().isAfter(endDate))
	            .map(r -> SalaryAccountUpdateRequestResponseDTO.builder()
	                    .requestId(r.getId())
	                    .employeeId(r.getEmployee().getId())
	                    .employeeName(r.getEmployee().getFullName())
	                    .bankName(r.getBankName())
	                    .accountNumber(r.getAccountNumber())
	                    .ifscCode(r.getIfscCode())
	                    .status(r.getStatus())
	                    .requestedAt(r.getRequestedAt())
	                    .build())
	            .collect(Collectors.toList());
	}

}
