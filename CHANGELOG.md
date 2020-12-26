# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
