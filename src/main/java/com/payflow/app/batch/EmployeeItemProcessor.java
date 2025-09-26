package com.payflow.app.batch;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.payflow.app.dto.request.CreateEmployeeRequestDTO;
import com.payflow.app.entity.BankAccount;
import com.payflow.app.entity.Employee;
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

	// In a real-world scenario, you might cache the organization lookup to reduce
	// DB hits
	// but for simplicity, we'll use findById here.
	private Organization getOrganization(Long orgId) {
		return organizationRepository.findById(orgId)
				.orElseThrow(() -> new NotFoundException("Organization not found with id: " + orgId));
	}

	@Override
	public Employee process(CreateEmployeeRequestDTO req) throws Exception {
		// 1. Map DTO to Employee Entity
		Employee employee = modelMapper.map(req, Employee.class);
		employee.setId(null);

		// 2. Set Organization (from DTO's organizationId)
		Organization org = getOrganization(req.getOrganizationId());
		employee.setOrganization(org);

		// 3. Build and link User entity
		User user = User.builder().username(employee.getEmployeeCode()) // using employeeCode as username
				.email(employee.getEmail()).passwordHash(encoder.encode(employee.getEmployeeCode() + "123")) // Default
																												// password
				.role(Role.EMPLOYEE).employee(employee) // Bi-directional link
				.mustResetPassword(true).enabled(true)
				// .organization(org) // Optional: Link User directly to Organization
				.build();

		employee.setUser(user); // Set bi-directional link
		// Note: We don't save the User here; it will be saved in the Writer.

		// 4. Handle Bank Account
		if (req.getBankAccount() != null) {
			BankAccount bankAccount = BankAccount.builder().employee(employee).organization(org) // Assuming BankAccount
																									// links to
																									// Organization as
																									// well (from your
																									// Organization
																									// entity)
					.ownerType(Role.EMPLOYEE).accountNumberEnc(req.getBankAccount().getAccountNumber())
					.ifsc(req.getBankAccount().getIfsc()).status("ACTIVE").build();

			employee.setBankAccounts(List.of(bankAccount));
			// Set the bi-directional link from bankAccount back to employee's list
			// employee.getBankAccounts().add(bankAccount);
		}

		// The result is an Employee entity ready for persistence
		return employee;
	}
}