package com.payflow.app.controller;

import java.io.File;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.payflow.app.dto.request.CreateEmployeeRequestDTO;
import com.payflow.app.dto.request.EmployeeSalaryStructureRequestDTO;
import com.payflow.app.dto.response.EmployeeResponseDTO;
import com.payflow.app.dto.response.EmployeeSalaryStructureResponseDTO;
import com.payflow.app.service.EmployeeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

	private final EmployeeService employeeService;
	private final JobLauncher jobLauncher;
	private final Job employeeCsvJob; // this is your batch job bean

	// @PostMapping("/upload")
	// @PreAuthorize("hasAuthority('ORG_ADMIN')")
//	public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
//		try {
//			// Save the uploaded file temporarily
//			String filePath = System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename();
//			file.transferTo(new java.io.File(filePath));
//
//			// Pass the file path and unique timestamp as JobParameters
//			JobParameters jobParameters = new JobParametersBuilder().addString("filePath", filePath)
//					.addLong("time", System.currentTimeMillis()).toJobParameters();
//
//			jobLauncher.run(employeeCsvJob, jobParameters);
//
//			return ResponseEntity.ok("CSV file uploaded and batch job started.");
//		} catch (Exception e) {
//			e.printStackTrace();
//			return ResponseEntity.internalServerError().body("Failed to start batch job: " + e.getMessage());
//		}
//	}
	//  public ResponseEntity<String> uploadCsv(
	//             @RequestParam("file") MultipartFile file,
	//             @RequestParam("organizationId") Long organizationId) {
	//         try {
	//             // Save file temporarily
	//             String filePath = System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename();
	//             file.transferTo(new File(filePath));

	//             // Pass filePath + orgId as job params
	//             JobParameters jobParameters = new JobParametersBuilder()
	//                     .addString("filePath", filePath)
	//                     .addLong("organizationId", organizationId)
	//                     .addLong("time", System.currentTimeMillis()) // unique run id
	//                     .toJobParameters();

	//             jobLauncher.run(employeeCsvJob, jobParameters);

	//             return ResponseEntity.ok("CSV uploaded. Batch job started.");
	//         } catch (Exception e) {
	//             e.printStackTrace();
	//             return ResponseEntity.internalServerError()
	//                     .body("Failed to start batch job: " + e.getMessage());
	//         }
	//     }

	// ORG_ADMIN: Create employee
	@PostMapping
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	public ResponseEntity<EmployeeResponseDTO> create(@Valid @RequestBody CreateEmployeeRequestDTO req) {
		return ResponseEntity.ok(employeeService.createEmployee(req));
	}

	// ORG_ADMIN: Get all employees
	@GetMapping
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	public ResponseEntity<List<EmployeeResponseDTO>> listAll() {
		return ResponseEntity.ok(employeeService.getAllEmployees());
	}

	// ORG_ADMIN: Get employee by ID
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	public ResponseEntity<EmployeeResponseDTO> getById(@PathVariable Long id) {
		return ResponseEntity.ok(employeeService.getEmployeeById(id));
	}

	// ORG_ADMIN: Update employee
	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	public ResponseEntity<EmployeeResponseDTO> update(@PathVariable Long id,
			@Valid @RequestBody CreateEmployeeRequestDTO req) {
		return ResponseEntity.ok(employeeService.updateEmployee(id, req));
	}

	// ORG_ADMIN: Delete employee
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		employeeService.deleteEmployee(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{id}/salary-structures")
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	public ResponseEntity<EmployeeSalaryStructureResponseDTO> addSalaryStructure(@PathVariable Long id,
			@Valid @RequestBody EmployeeSalaryStructureRequestDTO req) {
		return ResponseEntity.ok(employeeService.addSalaryStructure(id, req));
	}

	// ORG_ADMIN: Get all salary structures for employee
	@GetMapping("/{id}/salary-structures")
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	public ResponseEntity<List<EmployeeSalaryStructureResponseDTO>> getSalaryStructures(@PathVariable Long id) {
		return ResponseEntity.ok(employeeService.getSalaryStructures(id));
	}
}
