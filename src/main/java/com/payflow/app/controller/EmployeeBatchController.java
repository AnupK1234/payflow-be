package com.payflow.app.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/employees/batch")
@RequiredArgsConstructor
@Slf4j
public class EmployeeBatchController {

	private final JobLauncher jobLauncher;
	private final Job importEmployeeJob;

	/**
	 * API to upload a CSV file and launch the employee import batch job.
	 * 
	 * @param file           The CSV file containing employee data.
	 * @param organizationId The ID of the organization to link the employees to.
	 * @return A response indicating the job status.
	 */
	@PostMapping("/import")
	@Operation(
	        summary = "Batch Process employee creation",
	        description = "Input a csv file of employee data and the api creates the employee in db in batches"
	    )
	public ResponseEntity<String> importEmployees(@RequestParam("file") MultipartFile file,
			@RequestParam("organizationId") Long organizationId) {

		Path tempFilePath = null;
		try {
			// 1. Create a temporary file to hold the uploaded data
			tempFilePath = Files.createTempFile("employee_import_", ".csv");
			File tempFile = tempFilePath.toFile();

			// Write the MultipartFile content to the temp file
			try (InputStream inputStream = file.getInputStream();
					FileOutputStream outputStream = new FileOutputStream(tempFile)) {

				byte[] buffer = new byte[1024];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
			}

			// 2. Build Job Parameters
			// The unique job key ensures that a new job instance is created every time.
			// The file path parameter tells the ItemReader where to find the CSV.
			// The organizationId parameter is passed to the context for the Processor to
			// use.
			org.springframework.batch.core.JobParameters jobParameters = new JobParametersBuilder()
					.addLong("time", System.currentTimeMillis()) // Unique job instance key
					.addString("filePath", tempFilePath.toAbsolutePath().toString()) // Path to the temp CSV file
					.addLong("organizationId", organizationId).toJobParameters();

			// 3. Launch the Job
			jobLauncher.run(importEmployeeJob, jobParameters);

			return ResponseEntity.ok("Employee batch import job started successfully.");

		} catch (Exception e) {
			log.error("Batch import failed", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Employee batch import failed: " + e.getMessage());
		} finally {
			// 4. Clean up the temporary file (important!)
			if (tempFilePath != null) {
				try {
					Files.delete(tempFilePath);
				} catch (Exception e) {
					log.warn("Could not delete temporary file: {}", tempFilePath, e);
				}
			}
		}
	}
}