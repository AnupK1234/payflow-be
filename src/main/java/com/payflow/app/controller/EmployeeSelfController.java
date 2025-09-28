package com.payflow.app.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.payflow.app.dto.request.SalaryAccountUpdateRequestDTO;
import com.payflow.app.dto.response.EmployeeSalaryStructureResponseDTO;
import com.payflow.app.service.EmployeeSelfService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employee/self")
@RequiredArgsConstructor
public class EmployeeSelfController {

    private final EmployeeSelfService employeeSelfService;

    // Employee: Get own salary history
    @GetMapping("/salary-history")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<List<EmployeeSalaryStructureResponseDTO>> getSalaryHistory() {
        return ResponseEntity.ok(employeeSelfService.getSalaryHistory());
    }

    // Employee: Download salary history PDF
    @GetMapping("/salary-history/pdf")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public void downloadSalaryHistoryPdf(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            HttpServletResponse response) {

        employeeSelfService.downloadSalaryHistoryPdf(startDate, endDate, response);
    }

//    // Employee: Download salary history CSV
//    @GetMapping("/salary-history/csv")
//    @PreAuthorize("hasAuthority('EMPLOYEE')")
//    public void downloadSalaryHistoryCsv(
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
//            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
//            HttpServletResponse response) {
//
//        employeeSelfService.downloadSalaryHistoryCsv(startDate, endDate, response);
//    }

    // Employee: Request salary account update
    @PostMapping("/salary-account/update-request")
    @PreAuthorize("hasAuthority('EMPLOYEE')")
    public ResponseEntity<String> requestSalaryAccountUpdate(
            @RequestBody @Valid SalaryAccountUpdateRequestDTO requestDTO) {

        employeeSelfService.requestSalaryAccountUpdate(requestDTO);
        return ResponseEntity.ok("Salary account update request submitted successfully");
    }
}
