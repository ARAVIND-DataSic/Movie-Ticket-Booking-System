package com.movie.payment;

import java.math.BigDecimal;

public interface PaymentMethod {
    com.movie.models.payment.PaymentMethod getPaymentMethod();

    boolean processPayment(BigDecimal amount);
}
