# OBWriteDomesticConsent4DataAuthorisation

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**authorisationType** | [**AuthorisationTypeEnum**](#AuthorisationTypeEnum) | Type of authorisation flow requested. | 
**completionDateTime** | [**OffsetDateTime**](OffsetDateTime.md) | Date and time at which the requested authorisation flow must be completed.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00 |  [optional]

<a name="AuthorisationTypeEnum"></a>
## Enum: AuthorisationTypeEnum
Name | Value
---- | -----
ANY | &quot;Any&quot;
SINGLE | &quot;Single&quot;
