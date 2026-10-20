package com.bank.bankingsystemspringboot.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountRequestValidationTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validAccountRequestHasNoViolations() {
        var request = new AccountRequest("Alice", "NL001", new BigDecimal("100.00"));
        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void missingOwnerAndNegativeInitialBalanceAreInvalid() {
        var request = new AccountRequest(" ", "NL001", new BigDecimal("-1.00"));
        assertEquals(2, validator.validate(request).size());
    }

    @Test
    void negativeAndZeroTransactionsAreInvalid() {
        assertFalse(validator.validate(new MoneyRequest(BigDecimal.ZERO)).isEmpty());
        assertFalse(validator.validate(new MoneyRequest(new BigDecimal("-1.00"))).isEmpty());
    }

    @Test
    void precisionAndScaleAreValidated() {
        assertFalse(validator.validate(new MoneyRequest(new BigDecimal("1.001"))).isEmpty());
    }
}
