package com.payflow.app.service;

import java.time.LocalDate;
import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import com.payflow.app.dto.request.SalaryAccountUpdateRequestDTO;
import com.payflow.app.dto.response.EmployeeSalaryStructureResponseDTO;

public interface EmployeeSelfService {

    List<EmployeeSalaryStructureResponseDTO> getSalaryHistory();

    void downloadSalaryHistoryPdf(LocalDate startDate, LocalDate endDate, HttpServletResponse response);

    void downloadSalaryHistoryCsv(LocalDate startDate, LocalDate endDate, HttpServletResponse response);

	void requestSalaryAccountUpdate(@Valid SalaryAccountUpdateRequestDTO requestDTO);
}
