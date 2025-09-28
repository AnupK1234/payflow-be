package com.payflow.app.batch;

import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.payflow.app.entity.Employee;
import com.payflow.app.entity.User;
import com.payflow.app.repository.EmployeeRepository;
import com.payflow.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmployeeItemWriter implements ItemWriter<Employee> {

	private final EmployeeRepository employeeRepository;
	private final UserRepository userRepository;

	@Override
	public void write(Chunk<? extends Employee> chunk) throws Exception {

		// Extract Users from Employees for separate batch save
		List<User> users = chunk.getItems().stream().map(Employee::getUser).filter(user -> user != null).toList();

		// 1. Save all User entities in the chunk
		// Note: The User entity has a OneToOne relationship with Employee.
		// It's best to save related entities explicitly if they have their own
		// repositories
		// and separate unique constraints (like username, email in User).
		// Since User has unique constraints on username and email, saving them in a
		// batch
		// is generally safer than relying solely on cascade from Employee.
		userRepository.saveAll(users);

		// 2. Save all Employee entities in the chunk
		// This will cascade the BankAccounts save (due to CascadeType.ALL on
		// bankAccounts list)
		// and link the saved User entity (since User is already persisted).
		employeeRepository.saveAll(chunk.getItems());
	}
}