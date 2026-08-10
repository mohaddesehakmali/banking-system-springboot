package com.bank.bankingsystemspringboot.service;

import com.bank.bankingsystemspringboot.dto.AccountRequest;
import com.bank.bankingsystemspringboot.entity.Account;
import com.bank.bankingsystemspringboot.exception.ResourceNotFoundException;
import com.bank.bankingsystemspringboot.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(AccountRequest request) {
        if (request.getInitialBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative.");
        }

        Account account = new Account(
                request.getOwnerName(),
                request.getAccountNumber(),
                request.getInitialBalance()
        );

        return accountRepository.save(account);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id: " + id));
    }
}
