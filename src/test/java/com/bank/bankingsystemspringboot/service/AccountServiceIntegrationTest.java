package com.bank.bankingsystemspringboot.service;

import com.bank.bankingsystemspringboot.dto.AccountRequest;
import com.bank.bankingsystemspringboot.dto.AccountResponse;
import com.bank.bankingsystemspringboot.dto.TransferRequest;
import com.bank.bankingsystemspringboot.exception.DuplicateAccountException;
import com.bank.bankingsystemspringboot.exception.InsufficientFundsException;
import com.bank.bankingsystemspringboot.exception.ResourceNotFoundException;
import com.bank.bankingsystemspringboot.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountServiceIntegrationTest {
    @Autowired private AccountService service;
    @Autowired private AccountRepository repository;

    @BeforeEach
    void clearDatabase() {
        repository.deleteAll();
    }

    @Test
    void createsAndRetrievesAccount() {
        AccountResponse created = create("NL001", "125.50");
        assertNotNull(created.id());
        assertEquals("Alice", service.getAccountById(created.id()).ownerName());
        assertEquals(0, created.balance().compareTo(new BigDecimal("125.50")));
        assertEquals(1, service.getAllAccounts().size());
    }

    @Test
    void rejectsDuplicateAccountNumber() {
        create("NL001", "10.00");
        assertThrows(DuplicateAccountException.class, () -> create("NL001", "5.00"));
        assertEquals(1, repository.count());
    }

    @Test
    void rejectsNegativeAndOverpreciseInitialBalance() {
        assertThrows(IllegalArgumentException.class, () -> create("NL001", "-1.00"));
        assertThrows(IllegalArgumentException.class, () -> create("NL002", "1.999"));
    }

    @Test
    void depositsAndWithdrawsMoney() {
        AccountResponse account = create("NL001", "100.00");
        service.deposit(account.id(), new BigDecimal("20.00"));
        service.withdraw(account.id(), new BigDecimal("5.00"));
        assertBalance(account.id(), "115.00");
    }

    @Test
    void rejectsInvalidTransactionAmount() {
        AccountResponse account = create("NL001", "10.00");
        assertThrows(IllegalArgumentException.class, () -> service.deposit(account.id(), BigDecimal.ZERO));
        assertThrows(IllegalArgumentException.class, () -> service.withdraw(account.id(), new BigDecimal("-5.00")));
        assertThrows(IllegalArgumentException.class, () -> service.deposit(account.id(), new BigDecimal("1.001")));
        assertBalance(account.id(), "10.00");
    }

    @Test
    void rejectsOverdraftWithoutChangingBalance() {
        AccountResponse account = create("NL001", "10.00");
        assertThrows(InsufficientFundsException.class,
                () -> service.withdraw(account.id(), new BigDecimal("11.00")));
        assertBalance(account.id(), "10.00");
    }

    @Test
    void transfersBetweenAccountsAtomically() {
        AccountResponse from = create("NL001", "100.00");
        AccountResponse to = create("NL002", "15.00");
        var receipt = service.transfer(new TransferRequest(from.id(), to.id(), new BigDecimal("25.00")));
        assertEquals(0, receipt.amount().compareTo(new BigDecimal("25.00")));
        assertBalance(from.id(), "75.00");
        assertBalance(to.id(), "40.00");
    }

    @Test
    void insufficientFundsTransferPreservesBothBalances() {
        AccountResponse from = create("NL001", "10.00");
        AccountResponse to = create("NL002", "15.00");
        assertThrows(InsufficientFundsException.class,
                () -> service.transfer(new TransferRequest(from.id(), to.id(), new BigDecimal("11.00"))));
        assertBalance(from.id(), "10.00");
        assertBalance(to.id(), "15.00");
    }

    @Test
    void preventsTransferToSameAccount() {
        AccountResponse account = create("NL001", "10.00");
        assertThrows(IllegalArgumentException.class,
                () -> service.transfer(new TransferRequest(account.id(), account.id(), BigDecimal.ONE)));
        assertBalance(account.id(), "10.00");
    }

    @Test
    void missingAccountProducesNotFoundException() {
        assertThrows(ResourceNotFoundException.class, () -> service.getAccountById(99999L));
        assertThrows(ResourceNotFoundException.class, () -> service.deposit(99999L, BigDecimal.ONE));
    }

    private AccountResponse create(String number, String amount) {
        return service.createAccount(new AccountRequest("Alice", number, new BigDecimal(amount)));
    }

    private void assertBalance(Long id, String expected) {
        assertEquals(0, service.getAccountById(id).balance().compareTo(new BigDecimal(expected)));
    }
}
