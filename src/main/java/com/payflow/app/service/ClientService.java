package com.payflow.app.service;

import java.util.List;
import com.payflow.app.dto.request.ClientRequestDTO;
import com.payflow.app.dto.response.ClientResponseDTO;

import jakarta.servlet.http.HttpServletRequest;

public interface ClientService {

    ClientResponseDTO createClient(ClientRequestDTO req, HttpServletRequest request);

    List<ClientResponseDTO> getAllClients(Long organizationId);

    ClientResponseDTO getClientById(Long id, Long organizationId);

    ClientResponseDTO updateClient(Long id, ClientRequestDTO req, HttpServletRequest request);

    void deleteClient(Long id, Long organizationId);
}
