package org.example.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record Money(BigDecimal amount, String currency) {
    public Money(BigDecimal amount, String currency) {
        if (amount == null || currency == null)
            throw new IllegalArgumentException("Amount and Currency must not be null");
        this.amount = amount.setScale(2, RoundingMode.HALF_EVEN);
        this.currency = currency;
    }

    public static Money of(double amount, String currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }

    public Money add(Money other) {
        checkCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    public Money subtract(Money other) {
        checkCurrency(other);
        return new Money(amount.subtract(other.amount), currency);
    }

    public Money multiply(double factor) {
        return new Money(amount.multiply(BigDecimal.valueOf(factor)), currency);
    }

    private void checkCurrency(Money other) {
        if (!currency.equals(other.currency))
            throw new IllegalArgumentException("Currency mismatch");
    }
}
