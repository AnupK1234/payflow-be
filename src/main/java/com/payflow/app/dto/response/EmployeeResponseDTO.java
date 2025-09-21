package com.payflow.app.dto.response;

import java.time.LocalDate;
import java.util.List;

import com.payflow.app.dto.request.EmployeeSalaryStructureRequestDTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponseDTO {

    private Long id;
    private String fullName;
    private String email;
    private String employeeCode;
    private LocalDate dateOfJoining;
    private String jobTitle;
    private String department;
    private String status;
    private String bankAccountNumber;
    private String ifscCode;
    private String aadhaarNumber;
    private String panNumber;
    private String organizationName; 
    private List<EmployeeSalaryStructureRequestDTO> salaryStructures; 
    
}
