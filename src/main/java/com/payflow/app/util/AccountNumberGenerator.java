package com.payflow.app.util;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payflow.app.repository.BankAccountRepository;

@Service
public class AccountNumberGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final long MAX = 999_999_999_999L;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    public String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            accountNumber = String.format("%012d", Math.abs(random.nextLong() % (MAX + 1)));
        } while (bankAccountRepository.existsByAccountNumberEnc(accountNumber));
        return accountNumber;
    }
}
