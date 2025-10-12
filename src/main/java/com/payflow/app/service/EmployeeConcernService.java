package com.payflow.app.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.payflow.app.dto.request.UpdateConcernStatusRequestDTO;
import com.payflow.app.dto.response.ConcernResponseDTO;

public interface EmployeeConcernService {
	ConcernResponseDTO raiseConcern(String dataJson, MultipartFile attachment);

	ConcernResponseDTO updateConcernStatus(Long concernId, UpdateConcernStatusRequestDTO requestDTO);

	List<ConcernResponseDTO> getConcernsByEmployee(Long employeeId);

	List<ConcernResponseDTO> getConcernsByOrganization(Long organizationId);
	
	Long getOrganizationIdByEmployeeId(Long employeeId);
}
