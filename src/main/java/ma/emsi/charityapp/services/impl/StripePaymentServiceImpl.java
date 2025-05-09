package ma.emsi.charityapp.services.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import ma.emsi.charityapp.dtos.DonDto;
import ma.emsi.charityapp.services.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripePaymentServiceImpl implements PaymentService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Override
    public String createPaymentIntent(DonDto donDto) throws Exception {
        try {
            Stripe.apiKey = stripeSecretKey;

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount((long) (donDto.getMontant() * 100)) // Stripe utilise les centimes
                    .setCurrency("mad")
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build()
                    )
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);
            return paymentIntent.getClientSecret();
        } catch (StripeException e) {
            throw new Exception("Erreur lors de la création du paiement: " + e.getMessage());
        }
    }

    @Override
    public boolean confirmPayment(String paymentIntentId) throws Exception {
        try {
            Stripe.apiKey = stripeSecretKey;
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            return "succeeded".equals(paymentIntent.getStatus());
        } catch (StripeException e) {
            throw new Exception("Erreur lors de la confirmation du paiement: " + e.getMessage());
        }
    }
}