package com.bank.bankingsystemspringboot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.math.BigDecimal;

@Entity

public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ownerName;
    private String accountNumber;
    private BigDecimal balance;
    public Account() {}

    public Account(String ownerName, String accountNumber, BigDecimal balance) {
        this.ownerName = ownerName;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public Long getId() { return id; }

    public String getOwnerName() { return ownerName; }

    public String getAccountNumber() { return accountNumber; }

    public BigDecimal getBalance() { return balance; }

    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }

    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public void setBalance(BigDecimal balance) { this.balance = balance; }
}


