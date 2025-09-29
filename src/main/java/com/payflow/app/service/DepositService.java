package com.payflow.app.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.payflow.app.dto.request.CreateDepositRequest;
import com.payflow.app.dto.response.DepositResponse;
import com.payflow.app.enums.DepositStatus;

public interface DepositService {

    DepositResponse createDepositForOrg(Long orgId, CreateDepositRequest request);

    DepositResponse createDepositForClient(Long clientId, CreateDepositRequest request);

    DepositResponse approveDeposit(Long depositId, boolean approve);

    List<DepositResponse> listDepositsForLoggedInUser();

    Page<DepositResponse> listForBank(DepositStatus status, LocalDate startDate, LocalDate endDate, Pageable pageable);

    List<DepositResponse> listByOrg(Long orgId);

    List<DepositResponse> listByClient(Long clientId);

    Long getOrgIdForUsername(String username);

    Long getClientIdForUsername(String username);
}
