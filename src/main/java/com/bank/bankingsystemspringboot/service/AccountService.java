package com.bank.bankingsystemspringboot.service;

import com.bank.bankingsystemspringboot.dto.AccountRequest;
import com.bank.bankingsystemspringboot.dto.AccountResponse;
import com.bank.bankingsystemspringboot.dto.TransferRequest;
import com.bank.bankingsystemspringboot.dto.TransferResponse;
import com.bank.bankingsystemspringboot.entity.Account;
import com.bank.bankingsystemspringboot.exception.DuplicateAccountException;
import com.bank.bankingsystemspringboot.exception.InsufficientFundsException;
import com.bank.bankingsystemspringboot.exception.ResourceNotFoundException;
import com.bank.bankingsystemspringboot.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {
    private final AccountRepository repository;

    public AccountService(AccountRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AccountResponse createAccount(AccountRequest request) {
        if (request == null || request.ownerName() == null || request.ownerName().isBlank()
                || request.accountNumber() == null || request.accountNumber().isBlank()) {
            throw new IllegalArgumentException("Owner name and account number are required");
        }
        validateAmount(request.initialBalance(), true);
        String accountNumber = request.accountNumber().trim();
        if (repository.existsByAccountNumber(accountNumber)) {
            throw new DuplicateAccountException("Account number already exists: " + accountNumber);
        }
        Account account = new Account(request.ownerName().trim(), accountNumber, request.initialBalance());
        return toResponse(repository.saveAndFlush(account));
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getAllAccounts() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccountById(Long id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + id)));
    }

    @Transactional
    public AccountResponse deposit(Long id, BigDecimal amount) {
        validateAmount(amount, false);
        Account account = lockAccount(id);
        account.setBalance(account.getBalance().add(amount));
        return toResponse(repository.save(account));
    }

    @Transactional
    public AccountResponse withdraw(Long id, BigDecimal amount) {
        validateAmount(amount, false);
        Account account = lockAccount(id);
        requireFunds(account, amount);
        account.setBalance(account.getBalance().subtract(amount));
        return toResponse(repository.save(account));
    }

    @Transactional
    public TransferResponse transfer(TransferRequest request) {
        if (request == null || request.fromAccountId() == null || request.toAccountId() == null) {
            throw new IllegalArgumentException("Both account IDs are required");
        }
        validateAmount(request.amount(), false);
        long fromId = request.fromAccountId();
        long toId = request.toAccountId();
        if (fromId == toId) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        // Acquire locks in stable order, preventing lost updates and reducing deadlock risk.
        Account first = lockAccount(Math.min(fromId, toId));
        Account second = lockAccount(Math.max(fromId, toId));
        Account source = first.getId() == fromId ? first : second;
        Account destination = first.getId() == toId ? first : second;

        requireFunds(source, request.amount());
        source.setBalance(source.getBalance().subtract(request.amount()));
        destination.setBalance(destination.getBalance().add(request.amount()));
        repository.save(source);
        repository.save(destination);
        return new TransferResponse(toResponse(source), toResponse(destination), request.amount());
    }

    private Account lockAccount(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Account ID must be positive");
        }
        return repository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found: " + id));
    }

    private void requireFunds(Account account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds for account: " + account.getId());
        }
    }

    private void validateAmount(BigDecimal amount, boolean zeroAllowed) {
        if (amount == null || amount.scale() > 2 || amount.precision() - amount.scale() > 17
                || (zeroAllowed ? amount.signum() < 0 : amount.signum() <= 0)) {
            throw new IllegalArgumentException("Amount must be valid and have at most 2 decimal places");
        }
    }

    private AccountResponse toResponse(Account account) {
        return new AccountResponse(account.getId(), account.getOwnerName(),
                account.getAccountNumber(), account.getBalance());
    }
}
