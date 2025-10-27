package com.payflow.app.batch;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.payflow.app.dto.request.CreateEmployeeRequestDTO;
import com.payflow.app.entity.BankAccount;
import com.payflow.app.entity.Employee;
import com.payflow.app.entity.EmployeeSalaryStructure;
import com.payflow.app.entity.Organization;
import com.payflow.app.entity.User;
import com.payflow.app.enums.Role;
import com.payflow.app.exception.NotFoundException;
import com.payflow.app.repository.OrganizationRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmployeeItemProcessor implements ItemProcessor<CreateEmployeeRequestDTO, Employee> {

	private final OrganizationRepository organizationRepository;
	private final BCryptPasswordEncoder encoder;
	private final ModelMapper modelMapper;

	private Long organizationId; // Field to hold the organization ID injected from JobParameters
	private Organization cachedOrganization;

	@BeforeStep
	public void beforeStep(StepExecution stepExecution) {
		// Get the organizationId parameter passed from the controller via JobParameters
		this.organizationId = stepExecution.getJobParameters().getLong("organizationId");
		// Pre-fetch and cache the Organization entity since it's used for every
		// employee
		this.cachedOrganization = getOrganization(this.organizationId);
	}

	// In a real-world scenario, you might cache the organization lookup to reduce
	// DB hits
	// but for simplicity, we'll use findById here.
	private Organization getOrganization(Long orgId) {
		return organizationRepository.findById(orgId)
				.orElseThrow(() -> new NotFoundException("Organization not found with id: " + orgId));
	}

	@Override
	public Employee process(CreateEmployeeRequestDTO req) throws Exception {
		Organization org = this.cachedOrganization;

		// 1. Map DTO to Employee Entity
		Employee employee = modelMapper.map(req, Employee.class);
		employee.setId(null);

		// 2. Set Organization (from DTO's organizationId)
		employee.setOrganization(org);

		// 3. Build and link User entity
		User user = User.builder().username(employee.getEmployeeCode()) // using employeeCode as username
				.email(employee.getEmail()).passwordHash(encoder.encode(employee.getEmployeeCode() + "123")) // Default
																												// password
				.role(Role.EMPLOYEE).employee(employee) // Bi-directional link
				.mustResetPassword(true).enabled(true).build();

		employee.setUser(user); // Set bi-directional link
		// Note: We don't save the User here; it will be saved in the Writer.

		// 4. Handle Bank Account
		if (req.getBankAccount() != null) {
			BankAccount bankAccount = BankAccount.builder().employee(employee).organization(org)
					.ownerType(Role.EMPLOYEE).accountNumberEnc(req.getBankAccount().getAccountNumber())
					.ifsc(req.getBankAccount().getIfsc()).status("ACTIVE").build();

			employee.setBankAccounts(List.of(bankAccount));
		}

		if (req.getSalaryStructure() != null) {
			var s = req.getSalaryStructure();
			var basic = req.getSalaryStructure().getBasic();
			var hra = basic.multiply(BigDecimal.valueOf(0.25)); // 25%
			var da = basic.multiply(BigDecimal.valueOf(0.15)); // 15%
			var pf = basic.multiply(BigDecimal.valueOf(0.10)); // 10%
			var net = basic.add(hra).add(da).subtract(pf);

			EmployeeSalaryStructure structure = EmployeeSalaryStructure.builder()
					.effectiveFrom(s.getEffectiveFrom() != null ? s.getEffectiveFrom() : LocalDate.now()).basic(basic)
					.hra(hra).da(da).pf(pf).netSalary(net).isCurrent(true).employee(employee).build();

			employee.setSalaryStructures(List.of(structure));
		}

		return employee;
	}
}