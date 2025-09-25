package com.payflow.app.service;

import java.util.List;

import com.payflow.app.dto.request.CreateDepositRequest;
import com.payflow.app.dto.response.DepositResponse;

public interface DepositService {

    // Org Admin creates a deposit request
    DepositResponse createDeposit(Long orgId, CreateDepositRequest req);

    // Bank Admin approves or rejects a deposit request
    DepositResponse approveDeposit(Long depositId, boolean approve);

    // Org Admin lists all deposits for their organization
    List<DepositResponse> listByOrg(Long orgId);
}
