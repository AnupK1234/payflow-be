package com.payflow.app.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.payflow.app.dto.request.SalaryAccountUpdateRequestDTO;
import com.payflow.app.dto.response.EmployeeSalaryStructureResponseDTO;
import com.payflow.app.entity.Employee;
import com.payflow.app.entity.SalaryAccountUpdateRequest;
import com.payflow.app.entity.User;
import com.payflow.app.exception.NotFoundException;
import com.payflow.app.repository.EmployeeSalaryStructureRepository;
import com.payflow.app.repository.SalaryAccountUpdateRequestRepository;
import com.payflow.app.repository.UserRepository;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeSelfServiceImpl implements EmployeeSelfService {

	private final UserRepository userRepository;
	private final EmployeeSalaryStructureRepository salaryStructureRepository;
	private final SalaryAccountUpdateRequestRepository salaryAccountUpdateRequestRepository;
	private final org.modelmapper.ModelMapper modelMapper;

	// Get currently logged-in user
	private User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new NotFoundException("User not found with username: " + username));
	}

	// ---------------- Salary History ----------------
	@Override
	public List<EmployeeSalaryStructureResponseDTO> getSalaryHistory() {
		User user = getCurrentUser();
		Long employeeId = user.getEmployee().getId();

		return salaryStructureRepository.findByEmployeeId(employeeId).stream()
				.map(struct -> modelMapper.map(struct, EmployeeSalaryStructureResponseDTO.class))
				.collect(Collectors.toList());
	}

	@Override
	public void downloadSalaryHistoryPdf(LocalDate startDate, LocalDate endDate, HttpServletResponse response) {
		try {
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment; filename=salary_history.pdf");

			User user = getCurrentUser();
			Long employeeId = user.getEmployee().getId();

			// Fetch all salaries overlapping requested period
			List<EmployeeSalaryStructureResponseDTO> allSalaries = salaryStructureRepository
					.findByEmployeeId(employeeId).stream()
					.map(s -> modelMapper.map(s, EmployeeSalaryStructureResponseDTO.class)).filter(s -> {
						LocalDate effFrom = s.getEffectiveFrom();
						LocalDate effTo = s.getEffectiveTo() != null ? s.getEffectiveTo() : LocalDate.MAX;

						return (startDate == null || !effTo.isBefore(startDate))
								&& (endDate == null || !effFrom.isAfter(endDate));
					})

					.sorted(Comparator.comparing(EmployeeSalaryStructureResponseDTO::getEffectiveFrom).reversed())
					.collect(Collectors.toList());

			// Keep only the latest overlapping salaries for the requested period
			List<EmployeeSalaryStructureResponseDTO> latestSalaries = new ArrayList<>();
			LocalDate periodStart = startDate != null ? startDate : LocalDate.MIN;
			LocalDate periodEnd = endDate != null ? endDate : LocalDate.MAX;

			for (EmployeeSalaryStructureResponseDTO s : allSalaries) {
				LocalDate effFrom = s.getEffectiveFrom();
				LocalDate effTo = s.getEffectiveTo() != null ? s.getEffectiveTo() : LocalDate.MAX;

				// Check if salary overlaps any remaining period
				if (!effTo.isBefore(periodStart) && !effFrom.isAfter(periodEnd)) {
					latestSalaries.add(s);

					// Shrink the periodStart to exclude this salary from earlier ones
					if (effFrom.isAfter(periodStart)) {
						periodEnd = effFrom.minusDays(1);
					} else {
						break; // No more earlier salaries matter
					}
				}
			}

			PdfWriter writer = new PdfWriter(response.getOutputStream());
			PdfDocument pdfDoc = new PdfDocument(writer);
			Document document = new Document(pdfDoc);

			document.add(new Paragraph("Salary History for Employee: " + user.getEmployee().getFullName()).setBold()
					.setFontSize(16));
			document.add(new Paragraph("\n"));

			for (EmployeeSalaryStructureResponseDTO s : latestSalaries) {
				float[] columnWidths = { 150F, 250F };
				Table table = new Table(columnWidths);
				table.useAllAvailableWidth();

				table.addCell(new Cell().add(new Paragraph("Effective From").setBold()));
				table.addCell(new Cell().add(new Paragraph(s.getEffectiveFrom().toString())));

				table.addCell(new Cell().add(new Paragraph("Effective To").setBold()));
				table.addCell(new Cell()
						.add(new Paragraph(s.getEffectiveTo() != null ? s.getEffectiveTo().toString() : "-")));

				table.addCell(new Cell().add(new Paragraph("Basic").setBold()));
				table.addCell(new Cell().add(new Paragraph(String.valueOf(s.getBasic()))));

				table.addCell(new Cell().add(new Paragraph("HRA").setBold()));
				table.addCell(new Cell().add(new Paragraph(String.valueOf(s.getHra()))));

				table.addCell(new Cell().add(new Paragraph("DA").setBold()));
				table.addCell(new Cell().add(new Paragraph(String.valueOf(s.getDa()))));

				table.addCell(new Cell().add(new Paragraph("PF").setBold()));
				table.addCell(new Cell().add(new Paragraph(String.valueOf(s.getPf()))));

				table.addCell(new Cell().add(new Paragraph("Other Allowances").setBold()));
				table.addCell(
						new Cell().add(new Paragraph(s.getOtherAllowances() != null ? s.getOtherAllowances() : "-")));

				table.addCell(new Cell().add(new Paragraph("Net Salary").setBold()));
				table.addCell(new Cell().add(new Paragraph(String.valueOf(s.getNetSalary()))));

				table.addCell(new Cell().add(new Paragraph("Active").setBold()));
				table.addCell(
						new Cell().add(new Paragraph(s.getIsCurrent() != null && s.getIsCurrent() ? "Yes" : "No")));

				document.add(table);
				document.add(new Paragraph("\n")); // spacing
			}

			if (latestSalaries.isEmpty()) {
				document.add(new Paragraph("No salary found for the selected period."));
			}

			document.close();

		} catch (IOException e) {
			throw new RuntimeException("Error generating PDF: " + e.getMessage());
		}
	}

	// ---------------- Salary Account Update Request ----------------
	@Override
	public void requestSalaryAccountUpdate(@Valid SalaryAccountUpdateRequestDTO requestDTO) {
		User user = getCurrentUser();
		Employee employee = user.getEmployee();

		SalaryAccountUpdateRequest request = new SalaryAccountUpdateRequest();
		request.setEmployee(employee);
		request.setOrg(employee.getOrganization());
		request.setBankName(requestDTO.getBankName());
		request.setAccountNumber(requestDTO.getAccountNumber());
		request.setIfscCode(requestDTO.getIfscCode());
		request.setAdditionalInfo(requestDTO.getAdditionalInfo());
		request.setStatus("PENDING");
		request.setRequestedAt(java.time.LocalDateTime.now());

		salaryAccountUpdateRequestRepository.save(request);
	}

//	

	@Override
	public void downloadSalaryHistoryCsv(LocalDate startDate, LocalDate endDate, HttpServletResponse response) {
	    try {
	        response.setContentType("text/csv");
	        response.setHeader("Content-Disposition", "attachment; filename=salary_history.csv");

	        User user = getCurrentUser();
	        Long employeeId = user.getEmployee().getId();

	        List<EmployeeSalaryStructureResponseDTO> allSalaries = salaryStructureRepository
	                .findByEmployeeId(employeeId).stream()
	                .map(s -> modelMapper.map(s, EmployeeSalaryStructureResponseDTO.class))
	                .filter(s -> {
	                    LocalDate effFrom = s.getEffectiveFrom();
	                    LocalDate effTo = s.getEffectiveTo() != null ? s.getEffectiveTo() : LocalDate.MAX;

	                    return (startDate == null || !effTo.isBefore(startDate)) &&
	                           (endDate == null || !effFrom.isAfter(endDate));
	                })
	                .sorted(Comparator.comparing(EmployeeSalaryStructureResponseDTO::getEffectiveFrom).reversed())
	                .collect(Collectors.toList());

	        // Write header
	        StringBuilder sb = new StringBuilder();
	        sb.append("Effective From,Effective To,Basic,HRA,DA,PF,Other Allowances,Net Salary,Active\n");

	        for (EmployeeSalaryStructureResponseDTO s : allSalaries) {
	            sb.append(s.getEffectiveFrom()).append(",");
	            sb.append(s.getEffectiveTo() != null ? s.getEffectiveTo() : "-").append(",");
	            sb.append(s.getBasic()).append(",");
	            sb.append(s.getHra()).append(",");
	            sb.append(s.getDa()).append(",");
	            sb.append(s.getPf()).append(",");
	            sb.append(s.getOtherAllowances() != null ? s.getOtherAllowances() : "-").append(",");
	            sb.append(s.getNetSalary()).append(",");
	            sb.append((s.getIsCurrent() != null && s.getIsCurrent()) ? "Yes" : "No").append("\n");
	        }

	        if (allSalaries.isEmpty()) {
	            sb.append("No salary found for the selected period.\n");
	        }

	        response.getWriter().write(sb.toString());
	        response.getWriter().flush();

	    } catch (IOException e) {
	        throw new RuntimeException("Error generating CSV: " + e.getMessage());
	    }
	}

}
