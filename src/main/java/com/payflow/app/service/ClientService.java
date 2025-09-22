package com.payflow.app.service;

import java.util.List;

import com.payflow.app.dto.request.ClientRequestDTO;
import com.payflow.app.dto.response.ClientResponseDTO;

public interface ClientService {

    ClientResponseDTO createClient(ClientRequestDTO req);

    List<ClientResponseDTO> getAllClients();

    ClientResponseDTO getClientById(Long id);

    ClientResponseDTO updateClient(Long id, ClientRequestDTO req);

    void deleteClient(Long id);
}
