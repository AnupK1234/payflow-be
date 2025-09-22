package com.payflow.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.payflow.app.dto.request.ClientRequestDTO;
import com.payflow.app.dto.response.ClientResponseDTO;
import com.payflow.app.service.ClientService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    // ORG_ADMIN: Create client
    @PostMapping
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public ResponseEntity<ClientResponseDTO> create(@Valid @RequestBody ClientRequestDTO req) {
        return ResponseEntity.ok(clientService.createClient(req));
    }

    // ORG_ADMIN: Get all clients
    @GetMapping
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public ResponseEntity<List<ClientResponseDTO>> listAll() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    // ORG_ADMIN: Get client by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public ResponseEntity<ClientResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    // ORG_ADMIN: Update client
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public ResponseEntity<ClientResponseDTO> update(@PathVariable Long id,
                                                    @Valid @RequestBody ClientRequestDTO req) {
        return ResponseEntity.ok(clientService.updateClient(id, req));
    }

    // ORG_ADMIN: Delete client
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ORG_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}
