package com.payflow.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payflow.app.dto.request.RaiseConcernRequestDTO;
import com.payflow.app.dto.request.UpdateConcernStatusRequestDTO;
import com.payflow.app.dto.response.ConcernResponseDTO;
import com.payflow.app.entity.Employee;
import com.payflow.app.entity.EmployeeConcern;
import com.payflow.app.entity.Organization;
import com.payflow.app.enums.ConcernStatus;
import com.payflow.app.repository.EmployeeConcernRepository;
import com.payflow.app.repository.EmployeeRepository;
import com.payflow.app.repository.OrganizationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeConcernServiceImpl implements EmployeeConcernService {

	private final EmployeeConcernRepository concernRepository;
	private final EmployeeRepository employeeRepository;
	private final OrganizationRepository organizationRepository;
	private final CloudinaryService cloudinaryService;
	private final ModelMapper modelMapper;

	@Override
	public ConcernResponseDTO raiseConcern(String dataJson, MultipartFile attachment) {
		ObjectMapper mapper = new ObjectMapper();
		RaiseConcernRequestDTO requestDTO = null;
		try {
			requestDTO = mapper.readValue(dataJson, RaiseConcernRequestDTO.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		Employee employee = employeeRepository.findById(requestDTO.getEmployeeId())
				.orElseThrow(() -> new RuntimeException("Employee not found"));

		Organization organization = organizationRepository.findById(requestDTO.getOrganizationId())
				.orElseThrow(() -> new RuntimeException("Organization not found"));

		String attachmentUrl = null;
		if (attachment != null && !attachment.isEmpty()) {
			attachmentUrl = cloudinaryService.uploadFile(attachment);
		}

		EmployeeConcern concern = EmployeeConcern.builder().employee(employee).organization(organization)
				.description(requestDTO.getDescription()).attachmentUrl(attachmentUrl).status(ConcernStatus.OPEN)
				.createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();

		return modelMapper.map(concernRepository.save(concern), ConcernResponseDTO.class);
	}

	@Override
	public ConcernResponseDTO updateConcernStatus(Long concernId, UpdateConcernStatusRequestDTO requestDTO) {
		EmployeeConcern concern = concernRepository.findById(concernId)
				.orElseThrow(() -> new RuntimeException("Concern not found"));

		concern.setStatus(requestDTO.getStatus());
		concern.setUpdatedAt(LocalDateTime.now());

		return modelMapper.map(concernRepository.save(concern), ConcernResponseDTO.class);
	}

	@Override
	public List<ConcernResponseDTO> getConcernsByEmployee(Long employeeId) {
		return concernRepository.findByEmployeeId(employeeId).stream()
				.map(concern -> modelMapper.map(concern, ConcernResponseDTO.class)).collect(Collectors.toList());
	}

	@Override
	public List<ConcernResponseDTO> getConcernsByOrganization(Long organizationId) {
		return concernRepository.findByOrganizationId(organizationId).stream()
				.map(concern -> modelMapper.map(concern, ConcernResponseDTO.class)).collect(Collectors.toList());
	}
	
	@Override
	public Long getOrganizationIdByEmployeeId(Long employeeId) {
	    Employee employee = employeeRepository.findById(employeeId)
	            .orElseThrow(() -> new RuntimeException("Employee not found"));
	    return employee.getOrganization().getId();
	}

}
