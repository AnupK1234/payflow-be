package com.payflow.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payflow.app.dto.request.ClientRequestDTO;
import com.payflow.app.dto.response.ClientResponseDTO;
import com.payflow.app.service.ClientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Tag(name = "Client", description = "APIs for managing clients in the organization")
public class ClientController {

	private final ClientService clientService;

	// ORG_ADMIN: Create client
	@PostMapping
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	@Operation(summary = "Create a new client", description = "This endpoint allows ORG_ADMIN to create a new client in the system.")
	public ResponseEntity<ClientResponseDTO> create(@Valid @RequestBody ClientRequestDTO req) {
		return ResponseEntity.ok(clientService.createClient(req));
	}

	// ORG_ADMIN: Get all clients
	@GetMapping
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	@Operation(summary = "Get a list of all clients", description = "This endpoint returns a list of all clients managed by the organization.")
	public ResponseEntity<List<ClientResponseDTO>> listAll() {
		return ResponseEntity.ok(clientService.getAllClients());
	}

	// ORG_ADMIN: Get client by ID
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	@Operation(summary = "Get client details by ID", description = "Fetches details of a specific client identified by the given ID.")
	public ResponseEntity<ClientResponseDTO> getById(@PathVariable Long id) {
		return ResponseEntity.ok(clientService.getClientById(id));
	}

	// ORG_ADMIN: Update client
	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	@Operation(summary = "Update client details", description = "This endpoint allows ORG_ADMIN to update the details of an existing client identified by ID.")
	public ResponseEntity<ClientResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ClientRequestDTO req) {
		return ResponseEntity.ok(clientService.updateClient(id, req));
	}

	// ORG_ADMIN: Delete client
	@DeleteMapping("/{id}")
	@PreAuthorize("hasAuthority('ORG_ADMIN')")
	@Operation(summary = "Delete client by ID", description = "This endpoint allows ORG_ADMIN to delete a client from the system using the client's ID.")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		clientService.deleteClient(id);
		return ResponseEntity.noContent().build();
	}
}
