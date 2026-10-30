package com.movie.payment;

import com.movie.models.payment.PaymentMethod;
import java.math.BigDecimal;

public class UpiPayment implements com.movie.payment.PaymentMethod {
    private final String upiId;

    public UpiPayment(String upiId) {
        this.upiId = upiId == null ? "" : upiId.trim();
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.UPI;
    }

    @Override
    public boolean processPayment(BigDecimal amount) {
        return isPositive(amount)
                && upiId.matches("[A-Za-z0-9._-]+@[A-Za-z0-9.-]+");
    }

    private boolean isPositive(BigDecimal amount) {
        return amount != null && amount.signum() > 0;
    }
}
