package com.payflow.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payflow.app.dto.request.ClientRequestDTO;
import com.payflow.app.dto.request.BankAccountRequestDTO;
import com.payflow.app.dto.response.ClientResponseDTO;
import com.payflow.app.entity.BankAccount;
import com.payflow.app.entity.Client;
import com.payflow.app.entity.Organization;
import com.payflow.app.entity.User;
import com.payflow.app.enums.Role;
import com.payflow.app.exception.NotFoundException;
import com.payflow.app.repository.BankAccountRepository;
import com.payflow.app.repository.ClientRepository;
import com.payflow.app.repository.OrganizationRepository;
import com.payflow.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final OrganizationRepository organizationRepository;
    private final BankAccountRepository bankAccountRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Override
    public ClientResponseDTO createClient(ClientRequestDTO req) {
        // 1. Map Client
        Client client = modelMapper.map(req, Client.class);
        client.setId(null);

        // 2. Set Organization
        Organization org = organizationRepository.findById(req.getOrganizationId())
                .orElseThrow(() -> new NotFoundException("Organization not found with id: " + req.getOrganizationId()));
        client.setOrganization(org);

        // 3. Save client first
        client = clientRepository.save(client);

        // 4. Handle BankAccount (if provided)
        if (req.getBankAccount() != null) {
            BankAccountRequestDTO baReq = req.getBankAccount();

            BankAccount bankAccount = BankAccount.builder()
                    .client(client)
                    .ownerType(Role.CLIENT)
                    .accountNumberEnc(encrypt(baReq.getAccountNumber()))
                    .ifsc(baReq.getIfsc())
                    .status("ACTIVE")
                    .balance(0.0)
                    .build();

            bankAccountRepository.save(bankAccount);
        }

        // 5. Create linked User row
        User clientUser = User.builder()
                .username(client.getContactEmail()) // email as default username
                .email(client.getContactEmail())
                .passwordHash(encoder.encode(client.getContactEmail() + "123"))
                .role(Role.CLIENT)
                .client(client)
                .mustResetPassword(true)
                .enabled(true)
                .build();

        userRepository.save(clientUser);

        return modelMapper.map(client, ClientResponseDTO.class);
    }

    @Override
    public List<ClientResponseDTO> getAllClients() {
        return clientRepository.findAll().stream()
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

        // ✅ Handle new BankAccount if provided during update
        if (req.getBankAccount() != null && req.getBankAccount().getAccountNumber() != null) {
            // mark existing as INACTIVE
            bankAccountRepository.findByClientId(client.getId())
                    .ifPresent(acc -> acc.setStatus("INACTIVE"));

            // add new bank account
            BankAccount bankAccount = BankAccount.builder()
                    .client(client)
                    .ownerType(Role.CLIENT)
                    .accountNumberEnc(encrypt(req.getBankAccount().getAccountNumber()))
                    .ifsc(req.getBankAccount().getIfsc())
                    .status("ACTIVE")
                    .balance(0.0)
                    .build();

            bankAccountRepository.save(bankAccount);
        }

        client = clientRepository.save(client);
        return modelMapper.map(client, ClientResponseDTO.class);
    }

    @Override
    public void deleteClient(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found with id: " + id));

        client.setIsDeleted(true);
        clientRepository.save(client);
    }

    // --- helper encryption ---
    private String encrypt(String plain) {
        return plain; // TODO: replace with actual encryption logic
    }
}
