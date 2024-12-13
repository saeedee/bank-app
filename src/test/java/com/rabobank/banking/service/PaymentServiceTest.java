package com.rabobank.banking.service;

import com.rabobank.banking.model.Card;
import com.rabobank.banking.model.CardType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void testCalculateFeeForCreditCard() {
        Card card = new Card();
        card.setCardType(CardType.CREDIT);
        BigDecimal amount = BigDecimal.valueOf(100);

        BigDecimal fee = paymentService.calculateFee(amount, card);

        assertNotNull(fee);
        assertTrue(fee.compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testCalculateFeeForDebitCard() {
        Card card = new Card();
        card.setCardType(CardType.DEBIT);
        BigDecimal amount = BigDecimal.valueOf(100);

        BigDecimal fee = paymentService.calculateFee(amount, card);

        assertNotNull(fee);
        assertTrue(fee.compareTo(BigDecimal.ZERO) == 0);
    }

    @Test
    void testCalculateFeeForInvalidCardType() {
        Card card = new Card();
        card.setCardType(null);
        BigDecimal amount = BigDecimal.valueOf(100);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> paymentService.calculateFee(amount, card));
        assertEquals("Invalid card type: null", exception.getMessage());
    }
}
