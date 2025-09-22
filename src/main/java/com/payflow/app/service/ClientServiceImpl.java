package com.payflow.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payflow.app.dto.request.ClientRequestDTO;
import com.payflow.app.dto.response.ClientResponseDTO;
import com.payflow.app.entity.Client;
import com.payflow.app.entity.Organization;
import com.payflow.app.exception.NotFoundException;
import com.payflow.app.repository.ClientRepository;
import com.payflow.app.repository.OrganizationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final OrganizationRepository organizationRepository;
    private final ModelMapper modelMapper;

    @Override
    public ClientResponseDTO createClient(ClientRequestDTO req) {
        Client client = modelMapper.map(req, Client.class);
        client.setId(null);

        Organization org = organizationRepository.findById(req.getOrganizationId())
                .orElseThrow(() -> new NotFoundException("Organization not found with id: " + req.getOrganizationId()));
        client.setOrganization(org);

        client = clientRepository.save(client);
        return modelMapper.map(client, ClientResponseDTO.class);
    }

    @Override
    public List<ClientResponseDTO> getAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(client -> modelMapper.map(client, ClientResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public ClientResponseDTO getClientById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found with id: " + id));
        return modelMapper.map(client, ClientResponseDTO.class);
    }

    @Override
    public ClientResponseDTO updateClient(Long id, ClientRequestDTO req) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found with id: " + id));

        client.setCompanyName(req.getCompanyName());
        client.setContactPersonName(req.getContactPersonName());
        client.setContactEmail(req.getContactEmail());
        client.setContactPhone(req.getContactPhone());
        client.setAddress(req.getAddress());
        client.setCity(req.getCity());
        client.setState(req.getState());
        client.setCountry(req.getCountry());
        client.setPostalCode(req.getPostalCode());
        client.setStatus(req.getStatus());

        if (!client.getOrganization().getId().equals(req.getOrganizationId())) {
            Organization org = organizationRepository.findById(req.getOrganizationId())
                    .orElseThrow(() -> new NotFoundException("Organization not found with id: " + req.getOrganizationId()));
            client.setOrganization(org);
        }

        client = clientRepository.save(client);
        return modelMapper.map(client, ClientResponseDTO.class);
    }

    @Override
    public void deleteClient(Long id) {
        if (!clientRepository.existsById(id)) {
            throw new NotFoundException("Client not found with id: " + id);
        }
        clientRepository.deleteById(id);
    }
}
