package com.payflow.app.service;

import java.util.List;
import com.payflow.app.dto.request.ClientRequestDTO;
import com.payflow.app.dto.response.ClientResponseDTO;

public interface ClientService {

    ClientResponseDTO createClient(ClientRequestDTO req, Long organizationId);

    List<ClientResponseDTO> getAllClients(Long organizationId);

    ClientResponseDTO getClientById(Long id, Long organizationId);

    ClientResponseDTO updateClient(Long id, ClientRequestDTO req, Long organizationId);

    void deleteClient(Long id, Long organizationId);
}
