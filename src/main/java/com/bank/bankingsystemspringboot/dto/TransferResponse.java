package com.bank.bankingsystemspringboot.dto;

import java.math.BigDecimal;

public record TransferResponse(AccountResponse fromAccount, AccountResponse toAccount, BigDecimal amount) { }
