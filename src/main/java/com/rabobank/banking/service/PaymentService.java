package com.rabobank.banking.service;

import com.rabobank.banking.payment.CreditPaymentHandler;
import com.rabobank.banking.payment.DebitPaymentHandler;
import com.rabobank.banking.payment.PaymentHandler;
import com.rabobank.banking.model.Card;
import com.rabobank.banking.model.CardType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentService {

    // calculate the fee based on the card type and amount
    public BigDecimal calculateFee(BigDecimal amount, Card card) {
        PaymentHandler paymentHandler = getPaymentHandler(card.getCardType());
        return paymentHandler.calculateFee(amount);
    }

    // Factory method to select the appropriate fee calculation strategy based on card type
    private PaymentHandler getPaymentHandler(CardType cardType) {
        if (cardType == null) {
            throw new IllegalArgumentException("Invalid card type: null");
        }
        return switch (cardType) {
            case CREDIT -> new CreditPaymentHandler();
            case DEBIT -> new DebitPaymentHandler();
            default -> throw new IllegalArgumentException("Invalid card type: " + cardType);
        };
    }

}
