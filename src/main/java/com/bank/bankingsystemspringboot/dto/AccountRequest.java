package com.bank.bankingsystemspringboot.dto;

import java.math.BigDecimal;

public class AccountRequest {

    private String ownerName;
    private String accountNumber;
    private BigDecimal initialBalance;


    public String getOwnerName() {
        return ownerName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }
}
