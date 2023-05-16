# OBWriteDomesticResponse5DataMultiAuthorisation

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**status** | [**StatusEnum**](#StatusEnum) | Specifies the status of the authorisation flow in code form. | 
**numberRequired** | **Integer** | Number of authorisations required for payment order (total required at the start of the multi authorisation journey). |  [optional]
**numberReceived** | **Integer** | Number of authorisations received. |  [optional]
**lastUpdateDateTime** | [**OffsetDateTime**](OffsetDateTime.md) | Last date and time at the authorisation flow was updated.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00 |  [optional]
**expirationDateTime** | [**OffsetDateTime**](OffsetDateTime.md) | Date and time at which the requested authorisation flow must be completed.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00 |  [optional]

<a name="StatusEnum"></a>
## Enum: StatusEnum
Name | Value
---- | -----
AUTHORISED | &quot;Authorised&quot;
AWAITINGFURTHERAUTHORISATION | &quot;AwaitingFurtherAuthorisation&quot;
REJECTED | &quot;Rejected&quot;
