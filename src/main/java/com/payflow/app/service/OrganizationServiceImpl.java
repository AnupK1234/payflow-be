package com.payflow.app.service;
 
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
 
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
 
import com.payflow.app.dto.request.BankAccountRequestDTO;
import com.payflow.app.dto.request.CreateOrganizationRequest;
import com.payflow.app.dto.request.UpdateOrganizationRequest;
import com.payflow.app.dto.response.BankAccountResponseDTO;
import com.payflow.app.dto.response.DocumentResponse;
import com.payflow.app.dto.response.OrganizationResponse;
import com.payflow.app.entity.BankAccount;
import com.payflow.app.entity.Organization;
import com.payflow.app.entity.User;
import com.payflow.app.enums.Role;
import com.payflow.app.enums.Status;
import com.payflow.app.exception.NotFoundException;
import com.payflow.app.repository.OrganizationRepository;
import com.payflow.app.repository.UserRepository;
 
import lombok.RequiredArgsConstructor;
 
@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {
 
    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final ModelMapper modelMapper;
    private final EmailService emailService;
    private final DocumentService documentService;
 
    // ------------------ Helper ------------------
    private OrganizationResponse toResponse(Organization org) {
        OrganizationResponse res = modelMapper.map(org, OrganizationResponse.class);
        res.setStatus(org.getStatus().name());
 
        // Map bank account if exists
        if (org.getBankAccounts() != null && !org.getBankAccounts().isEmpty()) {
            org.getBankAccounts().stream()
                    .filter(acc -> "ACTIVE".equals(acc.getStatus()))
                    .findFirst()
                    .ifPresent(acc -> res.setBankAccount(
                            BankAccountResponseDTO.builder()
                                    .accountNumber(acc.getAccountNumberEnc())
                                    .ifsc(acc.getIfsc())
                                    .status(acc.getStatus())
                                    .build()
                    ));
        }
 
        // Map documents
        if (org.getDocuments() != null) {
            List<DocumentResponse> documentResponses = org.getDocuments().stream()
                    .map(doc -> modelMapper.map(doc, DocumentResponse.class))
                    .collect(Collectors.toList());
            res.setDocuments(documentResponses);
        }
 
        return res;
    }
 
    // ------------------ Organization CRUD ------------------
 
    @Override
    public OrganizationResponse registerOrganization(CreateOrganizationRequest req, List<MultipartFile> documents)
            throws IOException {
 
        Organization org = Organization.builder()
                .name(req.getName())
                .registrationNumber(req.getRegistrationNumber())
                .address(req.getAddress())
                .status(Status.PENDING)
                .build();
 
        // Handle bank account if provided
        BankAccountRequestDTO bankReq = req.getBankAccount();
        if (bankReq != null) {
            BankAccount bankAccount = BankAccount.builder()
                    .organization(org)
                    .ownerType(Role.ORG_ADMIN)
                    .accountNumberEnc(bankReq.getAccountNumber())
                    .ifsc(bankReq.getIfsc())
                    .build();
            org.setBankAccounts(List.of(bankAccount));
        }
 
        // Save organization (cascades bank accounts)
        org = organizationRepository.save(org);
 
        // Create admin user
        User admin = User.builder()
                .username(req.getAdminUsername())
                .email(req.getAdminEmail())
                .passwordHash(encoder.encode(req.getTempPassword()))
                .role(Role.ORG_ADMIN)
                .organization(org)
                .mustResetPassword(true)
                .enabled(true)
                .build();
        userRepository.save(admin);
        org.setAdminUser(admin);
 
        // Upload documents
        if (documents != null && !documents.isEmpty()) {
            int i = 1;
            for (MultipartFile file : documents) {
                documentService.uploadOrganizationDocument(file, "ORG_PROOF_" + i++, org);
            }
        }
 
        // Send welcome email
        Context context = new Context();
        context.setVariable("userName", admin.getUsername());
        emailService.sendEmailWithTemplate(req.getAdminEmail(), "Welcome to Our App!",
                "account-creation-template.html", context);
 
        return toResponse(org);
    }
 
    @Override
    public List<OrganizationResponse> listAll() {
        return organizationRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
 
    @Override
    public OrganizationResponse getById(Long id) {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Organization not found with id: " + id));
        return toResponse(org);
    }
 
    @Override
    public OrganizationResponse updateOrganization(Long id, UpdateOrganizationRequest req) {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Organization not found with id: " + id));
 
        if (req.getName() != null) org.setName(req.getName());
        if (req.getRegistrationNumber() != null) org.setRegistrationNumber(req.getRegistrationNumber());
        if (req.getAddress() != null) org.setAddress(req.getAddress());
 
        // Update bank account if provided
        BankAccountRequestDTO bankReq = req.getBankAccount();
        if (bankReq != null) {
            org.getBankAccounts().forEach(acc -> acc.setStatus("INACTIVE"));
            BankAccount bankAccount = BankAccount.builder()
                    .organization(org)
                    .ownerType(Role.ORG_ADMIN)
                    .accountNumberEnc(bankReq.getAccountNumber())
                    .ifsc(bankReq.getIfsc())
                    .build();
            org.getBankAccounts().add(bankAccount);
        }
 
        return toResponse(organizationRepository.save(org));
    }
 
    @Override
    public void deleteOrganization(Long id) {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Organization not found with id: " + id));
        organizationRepository.delete(org);
    }
 
    @Override
    public OrganizationResponse verifyOrganization(Long id, boolean approve) {
        Organization org = organizationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Organization not found with id: " + id));
 
        org.setStatus(approve ? Status.VERIFIED : Status.REJECTED);
        return toResponse(organizationRepository.save(org));
    }
}