package com.payflow.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.modelmapper.ModelMapper;

import com.payflow.app.dto.request.CreateEmployeeRequestDTO;
import com.payflow.app.dto.request.EmployeeSalaryStructureRequestDTO;
import com.payflow.app.dto.response.BankAccountResponseDTO;
import com.payflow.app.dto.response.EmployeeResponseDTO;
import com.payflow.app.dto.response.EmployeeSalaryStructureResponseDTO;
import com.payflow.app.entity.BankAccount;
import com.payflow.app.entity.Employee;
import com.payflow.app.entity.EmployeeSalaryStructure;
import com.payflow.app.entity.Organization;
import com.payflow.app.entity.User;
import com.payflow.app.enums.Role;
import com.payflow.app.exception.NotFoundException;
import com.payflow.app.repository.EmployeeRepository;
import com.payflow.app.repository.EmployeeSalaryStructureRepository;
import com.payflow.app.repository.OrganizationRepository;
import com.payflow.app.repository.UserRepository;

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
    
    
    @Override
    public EmployeeResponseDTO createEmployee(CreateEmployeeRequestDTO req) {
        // 1️⃣ Map EmployeeRequestDTO to Employee
        Employee employee = modelMapper.map(req, Employee.class);
        employee.setId(null); // ensure Hibernate treats it as new

        // 2️⃣ Set organization
        Organization org = organizationRepository.findById(req.getOrganizationId())
                .orElseThrow(() -> new NotFoundException(
                        "Organization not found with id: " + req.getOrganizationId()));
        employee.setOrganization(org);

        // 3️⃣ Handle bank account
        if (req.getBankAccount() != null) {
            BankAccount bankAccount = BankAccount.builder()
                    .employee(employee) // link to employee
                    .ownerType(Role.EMPLOYEE) // owner type
                    .accountNumberEnc(req.getBankAccount().getAccountNumber()) // store directly or encrypt
                    .ifsc(req.getBankAccount().getIfsc())
                    .status("ACTIVE")
                    .build();

            // Initialize employee's bank accounts list
            employee.setBankAccounts(List.of(bankAccount));
        }

        // 4️⃣ Save employee (cascades bank account)
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

        return modelMapper.map(employee, EmployeeResponseDTO.class);
    }

    @Override
    public EmployeeResponseDTO updateEmployee(Long id, CreateEmployeeRequestDTO req) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found with id: " + id));

        modelMapper.map(req, employee);

        if (!employee.getOrganization().getId().equals(req.getOrganizationId())) {
            Organization org = organizationRepository.findById(req.getOrganizationId())
                    .orElseThrow(() -> new NotFoundException(
                            "Organization not found with id: " + req.getOrganizationId()));
            employee.setOrganization(org);
        }

        // Handle bank account update
        if (req.getBankAccount() != null && req.getBankAccount().getAccountNumber() != null) {
            // Mark old accounts as INACTIVE
            employee.getBankAccounts().forEach(acc -> acc.setStatus("INACTIVE"));

            BankAccount bankAccount = BankAccount.builder()
                    .employee(employee)
                    .ownerType(Role.EMPLOYEE)
                    .accountNumberEnc(encrypt(req.getBankAccount().getAccountNumber()))
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
        return employeeRepository.findAll()
                .stream()
                .map(emp -> {
                    EmployeeResponseDTO dto = modelMapper.map(emp, EmployeeResponseDTO.class);
                    mapActiveBankAccountToResponse(emp, dto);
                    return dto;
                })
                .collect(Collectors.toList());
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
        employeeRepository.deleteById(id);
    }

    // ------------------ Salary Structures ------------------

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
        return salaryStructureRepository.findByEmployeeId(employeeId)
                .stream()
                .map(struct -> modelMapper.map(struct, EmployeeSalaryStructureResponseDTO.class))
                .collect(Collectors.toList());
    }

    // ------------------ Helper Methods ------------------

    private void mapActiveBankAccountToResponse(Employee employee, EmployeeResponseDTO response) {
        employee.getBankAccounts().stream()
                .filter(acc -> "ACTIVE".equals(acc.getStatus()))
                .findFirst()
                .ifPresent(ba -> response.setBankAccount(
                        BankAccountResponseDTO.builder()
                                .accountNumber(decrypt(ba.getAccountNumberEnc()))
                                .ifsc(ba.getIfsc())
                                .status(ba.getStatus())
                                .build()
                ));
    }

    private String encrypt(String plain) {
        return plain; // TODO: implement real encryption
    }

    private String decrypt(String enc) {
        return enc; // TODO: implement real decryption
    }
}
