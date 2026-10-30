package com.movie.CLI;

import com.movie.models.payment.Payment;
import com.movie.models.payment.PaymentStatus;
import com.movie.payment.CardPayment;
import com.movie.payment.NetBankingPayment;
import com.movie.payment.UpiPayment;
import com.movie.services.PaymentService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class PaymentCLI {
    private final PaymentService paymentService;

    public PaymentCLI(PaymentService paymentService) {
        this.paymentService = Objects.requireNonNull(paymentService, "Payment service is required.");
    }

    public Payment collectAndProcessPayment(int bookingId, int userId, List<BigDecimal> seatPrices,
            BigDecimal offerDiscount, Scanner input) {
        Objects.requireNonNull(input, "Input scanner is required.");
        BigDecimal total = paymentService.calculateTotalAmount(seatPrices, offerDiscount);
        System.out.println("Amount payable: ₹" + total);
        System.out.println("Choose payment method: 1) UPI  2) Card  3) Net Banking");
        int choice = input.nextInt();
        input.nextLine();

        com.movie.payment.PaymentMethod processor;
        switch (choice) {
            case 1:
                System.out.print("Simulated UPI ID: ");
                processor = new UpiPayment(input.nextLine());
                break;
            case 2:
                System.out.print("Simulated card authorization token (not card details): ");
                processor = new CardPayment(input.nextLine());
                break;
            case 3:
                System.out.print("Bank name: ");
                processor = new NetBankingPayment(input.nextLine());
                break;
            default:
                throw new IllegalArgumentException("Select a valid payment method.");
        }

        Payment payment = paymentService.createPayment(bookingId, userId, total, processor.getPaymentMethod());
        payment = paymentService.processPayment(payment.getId(), processor);
        System.out.println("Payment " + payment.getStatus().name().toLowerCase()
                + " (payment ID: " + payment.getId() + ").");
        if (payment.getStatus() == PaymentStatus.FAILED) {
            System.out.println("The booking was marked failed and its locked seats were released.");
        }
        return payment;
    }

    public List<Payment> showPaymentHistory(int bookingId) {
        List<Payment> history = paymentService.getPaymentHistory(bookingId);
        if (history.isEmpty()) {
            System.out.println("No payment history for booking " + bookingId + ".");
            return history;
        }
        for (Payment payment : history) {
            System.out.println("Payment " + payment.getId() + ": ₹" + payment.getAmount()
                    + " | " + payment.getMethod() + " | " + payment.getStatus()
                    + " | " + payment.getPaymentDate());
        }
        return history;
    }
}
