package com.payflow.app.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import com.payflow.app.batch.EmployeeFieldSetMapper;
import com.payflow.app.dto.request.CreateEmployeeRequestDTO;
import com.payflow.app.entity.Employee;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class BatchConfig {

	private final JobRepository jobRepository;
	private final PlatformTransactionManager transactionManager;

	@Bean
	@StepScope // This is crucial for accessing job parameters
	public FlatFileItemReader<CreateEmployeeRequestDTO> employeeItemReader(
			@Value("#{jobParameters['filePath']}") String filePath) {

		FlatFileItemReader<CreateEmployeeRequestDTO> reader = new FlatFileItemReader<>();

		// Dynamically set the Resource using the filePath parameter
		reader.setResource(new FileSystemResource(filePath));
		reader.setName("employeeCsvReader");
		reader.setLinesToSkip(1); // Skip the header row

		reader.setLineMapper(employeeLineMapper());
		
		reader.setSkippedLinesCallback(line -> {
	        if (line.trim().isEmpty()) {
	            System.out.println("Skipping empty line...");
	        }
	    });
		
		return reader;
	}

	// --- LineMapper Helper ---
	// Defines how a CSV line is tokenized and mapped to the DTO fields
	@Bean
	public LineMapper<CreateEmployeeRequestDTO> employeeLineMapper() {
		DefaultLineMapper<CreateEmployeeRequestDTO> lineMapper = new DefaultLineMapper<>();

		// 1. Tokenizer: Ensure names match DTO properties (including nested structure)
		DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();

		// 💡 IMPORTANT: Set the correct delimiter. Your CSV input uses a comma (,),
		// even though the header you pasted used tabs (\t). The error log input
		// suggests a comma-separated format.
		lineTokenizer.setDelimiter(","); // Change this to "\t" if your actual file uses tabs
		lineTokenizer.setStrict(false);
		lineTokenizer.setNames("fullName", "email", "employeeCode", "dateOfJoining", "jobTitle", "department", "status",
				"aadhaarNumber", "panNumber","bankAccount.accountNumber", "bankAccount.ifsc",
				"bankAccount.status", "basicSalary");

		// 2. Field Set Mapper: Use the custom implementation
		FieldSetMapper<CreateEmployeeRequestDTO> fieldSetMapper = new EmployeeFieldSetMapper();

		lineMapper.setLineTokenizer(lineTokenizer);
		lineMapper.setFieldSetMapper(fieldSetMapper);
		return lineMapper;
	}

	// --- 2. ItemProcessor (Inject the one defined below) ---
	// The processor maps the DTO to the Entity and performs business
	// logic/validation
	// It's defined as a separate component for clarity.

	// --- 3. ItemWriter (Inject the one defined below) ---
	// The writer handles the batch persistence of Employee entities
	// It's defined as a separate component.

	// --- 4. Step Definition ---
	@Bean
	public Step employeeImportStep(ItemReader<CreateEmployeeRequestDTO> reader,
			ItemProcessor<CreateEmployeeRequestDTO, Employee> processor, ItemWriter<Employee> writer) {

		return new StepBuilder("employeeImportStep", jobRepository)
				.<CreateEmployeeRequestDTO, Employee>chunk(10, transactionManager) // Process 10 items at a time
				.reader(reader).processor(processor).writer(writer).build();
	}

	// --- 5. Job Definition ---
	@Bean
	public Job importEmployeeJob(Step employeeImportStep) {
		return new JobBuilder("importEmployeeJob", jobRepository).start(employeeImportStep).build();
	}
}