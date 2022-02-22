# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [7.2.4] - 2022-02-22
### Changed
- Handle no content response for PISP createDomesticPaymentConsent

## [7.2.3] - 2022-02-04
### Changed
- Adding support for cop phase 2 scope.

## [7.2.2] - 2021-11-19
### Changed
- Use client_credential for domestic Vrp initiation and funds confirmation flows.
 As both of them are not tied to the consent creation flow.      

## [7.2.1] - 2021-11-19
### Changed
- Make `RestVrpClient` constructor public

## [7.2.0] - 2021-11-12
### Added
- Added `VrpClient` interface and its implementation which allows to create domestic VRP consent, 
  confirm availability of funds for a VRP, retrieve and delete existing VRP consent. Also, it allows operations with a VRP like submit a domestic VRP and retrieve existing VRP.

## [7.1.0] - 2021-11-08
### Added
- Added VRP Api specs of version 3.1.9
- Added OpenApiGen v3.3.4 dependency
### Changed
- Payment initiation models are generated in `com.transferwise.openbanking.client.api.payment.v3.model.payment` package
- Upgrade `swagger-codegen-cli` to v3.0.29

## [7.0.1] - 2021-08-02
### Changed
- Update the versions of various dependencies in the Gradle build configurations
- Improvements to the Gradle publishing process

## [7.0.0] - 2021-07-13
### Added 
- The `RegistrationClient` interface has a new method to call the delete client registration endpoint
- The `GetAccessTokenRequest` class has new constructors to make it easier to supply the scope of the request
### Changed
- The `RegistrationPermission` class has been renamed to `Scope` and moved to the `oauth.domain` package, to better
  correspond to what the class represents
- Update the versions of various dependencies and plugins in the Gradle build configuration  

## [6.0.0] - 2021-05-18
### Added
- The `AspspDetails` interface has a new method to return the transport certificate subject name, for use in client 
  registration requests, to allow easier customisation of this value per ASPSP. By default, 
  `getRegistrationTransportCertificateSubjectName` returns the certificate subject name in RFC 2253 format
- The `AspspDetails` interface has a new method to return the scopes to request for an access token to use for an
  authenticated call to the ASPSP's update registration API. By default, `getRegistrationAuthenticationScopes` returns 
  the `openid` scope along with whatever scopes are configured for the software statement
### Changed
- The `registrationAuthenticationRequiresOpenIdScope` method on the `AspspDetails` interface has been removed, the new 
  `getRegistrationAuthenticationScopes` method should be used instead to customise this behaviour
- Change the type of the `OBSupplementaryData1` model from a string to an object, to prevent JSON de-serialization
  errors when parsing a JSON string with an empty object value (`{}`) for the supplementary data field.
- Update the versions of various dependencies and plugins in the Gradle build configuration

## [5.1.0] - 2021-05-17
### Changed
- When a JSON processing error is encountered when de-serializing JSON in the `JacksonJsonConverter` class, wrap the 
  Jackson exception in a new `JsonReadException` class, and include the problematic JSON in the exception
- When a JSON processing error is encountered when serializing JSON an object in the `JacksonJsonConverter` class, wrap 
  the Jackson exception in a new `JsonWriteException` class, and include the problematic object in the exception  
- In the `RestPaymentClient` class, do the API call response de-serialization ourselves rather via the `JsonConverter` 
  functionality, rather than having the Spring `RestTemplate` functionality do it. Coupled with the above change, this 
  gives easy access to the full problematic JSON when  the response contains invalid JSON  

## [5.0.0] - 2021-04-20
### Added
- A new `JsonConverter` interface has been added, which deals with converting to and from JSON, with the 
  `JacksonJsonConverter` implementing the interface
### Changed
- The `JwtSigner` class now requires a `JsonConverter` to passed as a constructor argument. This replaces the 
  `ObjectMapper` optional argument
- The V3 `RestPaymentClient` class now requires a `JsonConverter` to be passed as a constructor argument. This is used
  to create the JSON request bodies explicitly rather than it being done internally by the `RestOperations` instance, 
  fixing an issue with null object fields being included in the serialised JSON request body
- Update the versions of various dependencies and plugins in the Gradle build configuration

## [4.0.0] - 2021-01-22
### Changed
- The payment model classes are now generated dynamically from the Open Banking specification swagger definitions,
  currently aligned to the 3.1.6 specification, dropping the custom model classes in favour of these generated ones
- Renamed the `getFinancialId` method on the `AspspDetails` interface to `getOrganisationId`, to better describe what
  it returns and how it is used
- Renamed the `getRegistrationIssuerUrl` method on the `AspspDetails` interface to `getRegistrationAudience`,
  to better describe what it returns and how it is used, and provide a more useful default implementation of returning 
  the ASPSP organisation ID
- Renamed the `getTokenIssuerUrl` method on the `AspspDetails` interface to `getPrivateKeyJwtAuthenticationAudience`,
  to better describe what it returns and how it is used, and provide a more useful default implementation of returning
  the token URL
- Update the versions of various dependencies and plugins in the Gradle build configuration
- Use the Maven Central repository in favour of the JCenter repository in the Gradle build configuration
### Added
- The `AspspDetails` interface has a new method to supply the `iss` claim in a registration request, to allow easy
  customisation of the value for ASPSPs that require a non standard value

## [3.0.0] - 2021-02-15
### Changed
- Refactor the classes which use the `TppConfiguration` class as a class variable, to instead take an instance of the
  class as a method parameter. This makes it easier to have multiple `TppConfiguration` in use at the same time,
  without having to create separate instances of the classes which use it.
- Rename the `TppConfiguration` class to `SoftwareStatementDetails`, to better describe what it now represents.
- Rename the `RegistrationRequestService` `generateRegistrationRequest` `softwareStatement` method parameter to better
  differentiate it and the `softwareStatementDetails` parameter.
- Change the `clientIdIssuedAt` and `clientSecretExpiresAt` fields in the `ClientRegistrationResponse` class from type
  `Integer` to type `String`, to handle ASPSPs that return timestamps in these fields instead of integers.
- When requesting an access token for the update client registration API call, decide whether or not to include `openid`
  in the scope parameter, based on the ASPSP integration details via the new 
  `registrationAuthenticationRequiresOpenIdScope` method.

## [2.0.2] - 2021-02-01
### Changed
- When requesting an access token for the update client registration API call, set the scope parameter to `payments`, 
  as certain ASPSPs require a scope value, not including `openid`, to be set .

## [2.0.1] - 2021-01-27
### Changed
- When requesting an access token for the update client registration API call, set the scope parameter to `openid`, as 
  certain ASPSPs require a scope value to be set. 

## [2.0.0] - 2021-01-20
### Added 
- The `RegistrationClient` interface has a new method to update an existing client registration with an ASPSP
- There is now support for multiple TPP redirect URLs, the `RegistrationRequestService` now supports generating a 
  registration request with multiple TPP redirect URLs, and the `PaymentClient` now supports multiple TPP redirect URLs
  when exchanging an authorization code as part of creating a domestic payment or checking funds availability
### Changed
- The `ID_TOKEN` value in the `ResponseType` enum is replaced with `CODE_AND_ID_TOKEN`, so that the enum defines only
  the possible values required for the v3.2 client registration API, where it gets used for
- The `AspspDetails` interface method `getResponseTypes` now returns only `CODE_AND_ID_TOKEN` by default, instead of 
  the separate `CODE` and `ID_TOKEN` values, to align the default value with the defaults of the v3.2 client 
  registration API
- The `TppConfiguration` class string field `redirectUrl` is replaced with a list of strings field `redirectUrls`  

## [1.0.1] - 2020-12-28
### Changed
- Updated various dependencies to the latest versions, notably updating to Spring 5.3. 

## [1.0.0] - 2020-12-20
### Added
- A new `RegistrationRequestService` class which can build the request for client registration with an ASPSP
### Changed
- The `registerClient` method in `RegistrationClient` now takes the unsigned registration claims as an argument, 
  instead of the already signed claims, and does the signing itself 
- The `registerClient` method in `RegistrationClient` now returns the registration response body as a data object with
  dedicated fields for the data, instead of a plain string
- The `KeySupplier` interface has been moved to the `security` package and has a new method to get the transport 
  certificate to use for an ASPSP
- The `TppConfiguration` class has a new property, for the permissions that the TPP has and wants to request when 
  registering as a client with an ASPSP
- The `AspspDetails` interface has new methods to describe additional integration details with the ASPSP, required for 
  building a client registration request. 

## [0.0.27] - 2020-12-18
- Improvements to the Gradle publishing process

## [0.0.26] - 2020-12-06
- Improvements to the Gradle publishing process

## [0.0.25] - 2020-12-03
### Changed 
- Prepare the library Gradle configuration for making the GitHub repository public.

## [0.0.24] - 2020-11-20
- First version of the library published to a public Maven repository.  
