# OBWriteDomesticStandingOrderConsentResponse6Data

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**consentId** | **String** | OB: Unique identification as assigned by the ASPSP to uniquely identify the consent resource. | 
**creationDateTime** | [**OffsetDateTime**](OffsetDateTime.md) | Date and time at which the resource was created.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00 | 
**status** | [**StatusEnum**](#StatusEnum) | Specifies the status of consent resource in code form. | 
**statusUpdateDateTime** | [**OffsetDateTime**](OffsetDateTime.md) | Date and time at which the resource status was updated.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00 | 
**permission** | [**PermissionEnum**](#PermissionEnum) | Specifies the Open Banking service request types. | 
**readRefundAccount** | [**ReadRefundAccountEnum**](#ReadRefundAccountEnum) | Specifies to share the refund account details with PISP |  [optional]
**cutOffDateTime** | [**OffsetDateTime**](OffsetDateTime.md) | Specified cut-off date and time for the payment consent.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00 |  [optional]
**charges** | [**List&lt;OBWriteDomesticConsentResponse5DataCharges&gt;**](OBWriteDomesticConsentResponse5DataCharges.md) |  |  [optional]
**initiation** | [**OBWriteDomesticStandingOrder3DataInitiation**](OBWriteDomesticStandingOrder3DataInitiation.md) |  | 
**authorisation** | [**OBWriteDomesticConsent4DataAuthorisation**](OBWriteDomesticConsent4DataAuthorisation.md) |  |  [optional]
**scASupportData** | [**OBWriteDomesticConsent4DataSCASupportData**](OBWriteDomesticConsent4DataSCASupportData.md) |  |  [optional]
**debtor** | [**OBDebtorIdentification1**](OBDebtorIdentification1.md) |  |  [optional]

<a name="StatusEnum"></a>
## Enum: StatusEnum
Name | Value
---- | -----
AUTHORISED | &quot;Authorised&quot;
AWAITINGAUTHORISATION | &quot;AwaitingAuthorisation&quot;
CONSUMED | &quot;Consumed&quot;
REJECTED | &quot;Rejected&quot;

<a name="PermissionEnum"></a>
## Enum: PermissionEnum
Name | Value
---- | -----
CREATE | &quot;Create&quot;

<a name="ReadRefundAccountEnum"></a>
## Enum: ReadRefundAccountEnum
Name | Value
---- | -----
NO | &quot;No&quot;
YES | &quot;Yes&quot;
