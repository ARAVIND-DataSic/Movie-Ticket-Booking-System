package com.movie.payment;

import com.movie.models.payment.PaymentMethod;
import java.math.BigDecimal;

public class NetBankingPayment implements com.movie.payment.PaymentMethod {
    private final String bankName;
    private final boolean simulatedAuthorization;

    public NetBankingPayment(String bankName) {
        this(bankName, true);
    }

    public NetBankingPayment(String bankName, boolean simulatedAuthorization) {
        this.bankName = bankName == null ? "" : bankName.trim();
        this.simulatedAuthorization = simulatedAuthorization;
    }

    @Override
    public PaymentMethod getPaymentMethod() {
        return PaymentMethod.NET_BANKING;
    }

    @Override
    public boolean processPayment(BigDecimal amount) {
        return amount != null && amount.signum() > 0
                && !bankName.isEmpty() && simulatedAuthorization;
    }
}
