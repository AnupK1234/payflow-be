package com.payflow.app.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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
import com.payflow.app.repository.UserRepository;
import com.payflow.app.repository.SalaryAccountUpdateRequestRepository;

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

        return salaryStructureRepository.findByEmployeeId(employeeId)
                .stream()
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

            List<EmployeeSalaryStructureResponseDTO> salaryList = salaryStructureRepository
                    .findByEmployeeId(employeeId)
                    .stream()
                    .map(struct -> modelMapper.map(struct, EmployeeSalaryStructureResponseDTO.class))
                    .filter(s -> {
                        boolean overlaps = true;
                        if (startDate != null) {
                            overlaps = !s.getEffectiveTo().isBefore(startDate); // end >= start
                        }
                        if (endDate != null) {
                            overlaps = overlaps && !s.getEffectiveFrom().isAfter(endDate); // start <= end
                        }
                        return overlaps;
                    })
                    .collect(Collectors.toList());

            PdfWriter writer = new PdfWriter(response.getOutputStream());
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Salary History for Employee: " + user.getEmployee().getFullName())
                    .setBold().setFontSize(14));
            document.add(new Paragraph("\n"));

            float[] columnWidths = {150F, 300F};
            Table table = new Table(columnWidths);
            table.useAllAvailableWidth();

            for (EmployeeSalaryStructureResponseDTO s : salaryList) {
                table.addCell(new Cell().add(new Paragraph("Effective From").setBold()));
                table.addCell(new Cell().add(new Paragraph(s.getEffectiveFrom().toString())));

                table.addCell(new Cell().add(new Paragraph("Effective To").setBold()));
                table.addCell(new Cell().add(new Paragraph(s.getEffectiveTo().toString())));

                table.addCell(new Cell().add(new Paragraph("Basic").setBold()));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(s.getBasic()))));

                table.addCell(new Cell().add(new Paragraph("HRA").setBold()));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(s.getHra()))));

                table.addCell(new Cell().add(new Paragraph("DA").setBold()));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(s.getDa()))));

                table.addCell(new Cell().add(new Paragraph("PF").setBold()));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(s.getPf()))));

                table.addCell(new Cell().add(new Paragraph("Other Allowances").setBold()));
                table.addCell(new Cell().add(new Paragraph(s.getOtherAllowances())));
                
             
                table.addCell(new Cell().add(new Paragraph("Net Salary").setBold()));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(s.getNetSalary()))));

                

                table.addCell(new Cell(1, 2).add(new Paragraph("\n"))); // separator row
            }

            document.add(table);
            document.close();

        } catch (IOException e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage());
        }
    }

    @Override
    public void downloadSalaryHistoryCsv(LocalDate startDate, LocalDate endDate, HttpServletResponse response) {
        try {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=salary_history.csv");

            User user = getCurrentUser();
            Long employeeId = user.getEmployee().getId();

            List<EmployeeSalaryStructureResponseDTO> salaryList = salaryStructureRepository
                    .findByEmployeeId(employeeId)
                    .stream()
                    .map(struct -> modelMapper.map(struct, EmployeeSalaryStructureResponseDTO.class))
                    .filter(s -> {
                        boolean overlaps = true;
                        if (startDate != null) {
                            overlaps = !s.getEffectiveTo().isBefore(startDate);
                        }
                        if (endDate != null) {
                            overlaps = overlaps && !s.getEffectiveFrom().isAfter(endDate);
                        }
                        return overlaps;
                    })
                    .collect(Collectors.toList());

            String header = "Effective From,Effective To,Basic,HRA,DA,PF,Other Allowances\n";
            response.getWriter().write(header);

            for (EmployeeSalaryStructureResponseDTO s : salaryList) {
                response.getWriter().write(
                        s.getEffectiveFrom() + "," +
                        s.getEffectiveTo() + "," +
                        s.getBasic() + "," +
                        s.getHra() + "," +
                        s.getDa() + "," +
                        s.getPf() + "," +
                        s.getOtherAllowances() + "\n"
                );
            }

        } catch (IOException e) {
            throw new RuntimeException("Error generating CSV: " + e.getMessage());
        }
    }

    // Salary Account Update Request
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
        request.setRequestedAt(LocalDateTime.now());

        salaryAccountUpdateRequestRepository.save(request);

        // Optional: notify org admin about the request
    }
}
