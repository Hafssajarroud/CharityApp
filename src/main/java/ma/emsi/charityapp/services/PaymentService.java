package ma.emsi.charityapp.services;

import ma.emsi.charityapp.dtos.DonDto;

public interface PaymentService {
    String createPaymentIntent(DonDto donDto) throws Exception;
    boolean confirmPayment(String paymentIntentId) throws Exception;
}