package com.bank.bankingsystemspringboot.dto;

import java.math.BigDecimal;

public record AccountResponse(Long id, String ownerName, String accountNumber, BigDecimal balance) { }
