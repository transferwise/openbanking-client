# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
