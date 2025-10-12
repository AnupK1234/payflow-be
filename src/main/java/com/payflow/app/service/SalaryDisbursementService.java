package com.payflow.app.service;

import org.springframework.data.domain.Page;
import com.payflow.app.dto.request.SalaryDisbursementRequestActionDTO;
import com.payflow.app.dto.response.SalaryDisbursementResponseDTO;

public interface SalaryDisbursementService {

    SalaryDisbursementResponseDTO createRequest(Long orgId);

    void approveRequest(Long requestId) throws Exception;

    void rejectRequest(Long requestId);

    void takeAction(Long requestId, SalaryDisbursementRequestActionDTO action) throws Exception;

    Page<SalaryDisbursementResponseDTO> listRequests(String status, int page, int size);
}
