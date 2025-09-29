package com.payflow.app.batch;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.payflow.app.entity.BankAccount;
import com.payflow.app.entity.Employee;
import com.payflow.app.entity.EmployeeSalaryStructure;
import com.payflow.app.entity.SalaryTransaction;
import com.payflow.app.enums.Role;
import com.payflow.app.repository.BankAccountRepository;
import com.payflow.app.repository.SalaryTransactionRepository;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SalaryDisbursementJobConfig {

	private final BankAccountRepository bankAccountRepo;
	private final SalaryTransactionRepository transactionRepo;

	@Bean(name = "salaryEmployeeItemReader")
	@StepScope
	public JpaPagingItemReader<Employee> employeeItemReader(EntityManagerFactory emf,
	        @Value("#{jobParameters['orgId']}") Long orgId) {
		return new JpaPagingItemReaderBuilder<Employee>().name("salaryEmployeeItemReader").entityManagerFactory(emf)
				.queryString("SELECT e FROM Employee e " + "JOIN e.salaryStructures s "
						+ "WHERE e.organization.id = :orgId " + "AND e.status = 'ACTIVE' " + "AND s.isCurrent = true")
				.parameterValues(Map.of("orgId", orgId))
				.pageSize(50).build();
	}

	@Bean
	public ItemProcessor<Employee, SalaryTransaction> employeeToTransactionProcessor() {
		return employee -> {
			EmployeeSalaryStructure structure = employee.getSalaryStructures().stream()
					.filter(EmployeeSalaryStructure::getIsCurrent).findFirst().orElseThrow();

			BigDecimal netSalary = structure.getBasic().add(structure.getHra()).add(structure.getDa())
					.subtract(structure.getPf());

			return SalaryTransaction.builder().employee(employee).organization(employee.getOrganization())
					.amount(netSalary).transactionDate(LocalDateTime.now()).status("PENDING").build();
		};
	}

	@Bean
	public ItemWriter<SalaryTransaction> salaryTransactionWriter() {
		return transactions -> {
			for (SalaryTransaction tx : transactions) {
				BankAccount orgAcc = bankAccountRepo
						.findByOrganizationAndOwnerTypeAndStatus(tx.getOrganization(), Role.ORG_ADMIN, "ACTIVE")
						.orElseThrow();

				BankAccount empAcc = bankAccountRepo
						.findByEmployeeAndOwnerTypeAndStatus(tx.getEmployee(), Role.EMPLOYEE, "ACTIVE").orElseThrow();

				if (orgAcc.getBalance() >= tx.getAmount().doubleValue()) {
					orgAcc.setBalance(orgAcc.getBalance() - tx.getAmount().doubleValue());
					empAcc.setBalance(empAcc.getBalance() + tx.getAmount().doubleValue());
					tx.setStatus("SUCCESS");
				} else {
					tx.setStatus("FAILED");
				}
				bankAccountRepo.save(orgAcc);
				bankAccountRepo.save(empAcc);
				transactionRepo.save(tx);
			}
		};
	}

	@Bean
	public Step salaryDisbursementStep(JobRepository jobRepository, PlatformTransactionManager transactionManager,
			JpaPagingItemReader<Employee> reader, ItemProcessor<Employee, SalaryTransaction> processor,
			ItemWriter<SalaryTransaction> writer) {
		return new StepBuilder("salaryDisbursementStep", jobRepository)
				.<Employee, SalaryTransaction>chunk(50, transactionManager).reader(reader).processor(processor)
				.writer(writer).build();
	}

	@Bean
	public Job salaryDisbursementJob(JobRepository jobRepository, Step salaryDisbursementStep) {
		return new JobBuilder("salaryDisbursementJob", jobRepository).start(salaryDisbursementStep).build();
	}
}
