package com.payflow.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payflow.app.entity.SalaryTransaction;

public interface SalaryTransactionRepository extends JpaRepository<SalaryTransaction, Long> {
}
