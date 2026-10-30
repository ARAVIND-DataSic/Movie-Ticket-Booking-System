package com.movie.payment;

import com.movie.models.payment.PaymentMethod;
import java.math.BigDecimal;

public class CardPayment implements com.movie.payment.PaymentMethod {
    private final String simulatedAuthorizationToken;

    public CardPayment(String simulatedAuthorizationToken) {
        this.simulatedAuthorizationToken = simulatedAuthorizationToken == null
                ? "" : simulatedAuthorizationToken.trim();
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.CARD;
    }

    @Override
    public boolean processPayment(BigDecimal amount) {
        return amount != null && amount.signum() > 0
                && !simulatedAuthorizationToken.isEmpty()
                && !simulatedAuthorizationToken.startsWith("DECLINED:");
    }
}
