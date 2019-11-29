package com.transferwise.openbanking.client.api.payment.v1;

import com.transferwise.openbanking.client.api.payment.v1.domain.PaymentSetupResponse;
import com.transferwise.openbanking.client.api.payment.v1.domain.SubmitPaymentRequest;
import com.transferwise.openbanking.client.aspsp.AspspDetails;
import com.transferwise.openbanking.client.api.payment.v1.domain.PaymentSubmissionResponse;
import com.transferwise.openbanking.client.api.payment.v1.domain.SetupPaymentRequest;

public interface PaymentClient {

    PaymentSetupResponse setupPayment(SetupPaymentRequest setupPaymentRequest, AspspDetails aspspDetails);

    PaymentSubmissionResponse submitPayment(SubmitPaymentRequest submitPaymentRequest,
                                            String authorizationCode,
                                            AspspDetails aspspDetails);

    PaymentSubmissionResponse getPaymentSubmission(String paymentSubmissionId, AspspDetails aspspDetails);
}
