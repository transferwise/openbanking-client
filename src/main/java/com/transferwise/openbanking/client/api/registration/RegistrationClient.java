package com.transferwise.openbanking.client.api.registration;

import com.transferwise.openbanking.client.aspsp.AspspDetails;

public interface RegistrationClient {

    String registerClient(String signedClaims, AspspDetails aspspDetails);
}
