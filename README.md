# openbanking-client

![Apache 2](https://img.shields.io/hexpm/l/plug.svg)
![Java 1.8](https://img.shields.io/badge/Java-11-blue.svg)

Java client for using the Open Banking API, exposed by an ASPSP, as a TPP. The library supports a subset of the full 
API:  

- Support for version 3 [dynamic client registration](https://openbankinguk.github.io/dcr-docs-pub/v3.2/dynamic-client-registration.html)
- Support for version 3 [single immediate domestic payments](https://openbankinguk.github.io/read-write-api-site3/v3.1.5/profiles/payment-initiation-api-profile.html)
- Support for the following OAuth client authentication methods
    - Mutual TLS
    - Private key JWT
    - Client secret basic
    - Client secret post

## Example Usage

### Client Registration

```java
//
// Step 1 - configure the RegistrationClient instance to use for the ASPSP
//

TppConfiguration tppConfiguration = TppConfiguration.builder()
    // set the properties acording to your organisation
    .build();
// configure the SSL context of the instance according to your setup, including a KeyManager to support mutual TLS on
// conections to ASPSP and a TrustManager to support connections to ASPSPs using OB issued certificates
RestTemplate restTemplate = new RestTemplate();

// an example implementation might look up the values to supply from a KeyStore
KeySupplier signingKeySupplier = new ExampleKeySupplier();
JwtClaimsSigner jwtClaimsSigner = new JwtClaimsSigner(signingKeySupplier, tppConfiguration);

RegistrationClient registrationClient = new RestRegistrationClient(jwtClaimsSigner, restTemplate);

RegistrationRequestService registrationRequestService = new RegistrationRequestService(keySupplier, tppConfiguration);

// supplies the details of the ASPSP implementation required to make the API calls
AspspDetails aspspDetails = new ExampleAspspDetails();

// 
// Step 2 - register with the ASPSP
// 

ClientRegistrationRequest clientRegistrationRequest = registrationRequestService.generateRegistrationRequest(
        softwareStatement, 
        aspspDetails);

ClientRegistrationResponse clientRegistrationResponse = registrationClient.registerClient(clientRegistrationRequest, 
        aspspDetails);
```

### V3 Payments

```java
//
// Step 1 - configure the V3 PaymentClient instance to use for the ASPSP
//

TppConfiguration tppConfiguration = TppConfiguration.builder()
    // set the properties acording to your organisation
    .build();
// configure the SSL context of the instance according to your setup, including a KeyManager to support mutual TLS on
// conections to ASPSP and a TrustManager to support connections to ASPSPs using OB issued certificates
RestTemplate restTemplate = new RestTemplate();

ClientAuthentication tlsClientAuthentication = new TlsAuthentication();
OAuthClient restOAuthClient = new RestOAuthClient(tlsClientAuthentication, restTemplate);

// an example implementation might use the EndToEndIdentification of the request as the idempotency key 
IdempotencyKeyGenerator idempotencyKeyGenerator = new ExampleIdempotencyKeyGenerator();

// an example implementation might look up the values to supply from a KeyStore
KeySupplier signingKeySupplier = new ExampleKeySupplier();
JwtClaimsSigner jwtClaimsSigner = new JwtClaimsSigner(signingKeySupplier, tppConfiguration);

PaymentClient paymentClient = new RestPaymentClient(tppConfiguration,
    restTemplate,
    tlsClientAuthentication,
    idempotencyKeyGenerator,
    jwtClaimsSigner);

// supplies the details of the ASPSP implementation required to make the API calls
AspspDetails aspspDetails = new ExampleAspspDetails();

// 
// Step 2 - initiate the payment
// 

// set the properties according to the payment attempt
OBWriteDomesticConsent4 paymentConsentRequest = new OBWriteDomesticConsent4();
OBWriteDomesticConsentResponse5 paymentConsentResponse = paymentClient.createDomesticPaymentConsent(
    paymentConsentRequest, 
    aspspDetails);

// 
// Step 3 - redirect the user to the ASPSP authorisation site to authorise the payment 
// 
// on authorisation success, an authorization code is received from the ASPSP
// note, when building the ASPSP authorisation URL, the JwtClaimsSigner can be used to generate the request parameter
//  

//
// Step 4 - submit the payment for execution
//

//
// Step 4a (optional) - check for sufficient funds on the source account 
//

OBWriteFundsConfirmationResponse1 fundsConfirmationResponse = paymentClient.getFundsConfirmation(consentId, 
    authorizationCode, 
    aspspDetails);

// set the properties according to the payment attempt
OBWriteDomestic2 paymentRequest = new OBWriteDomestic2();
OBWriteDomesticResponse5 paymentResponse = paymentClient.createDomesticPayment(paymentRequest, 
    authorizationCode, 
    aspspDetails);
```

## License

Copyright 2019 TransferWise Ltd.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
