# OBWriteDomesticStandingOrderResponse6Data

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**domesticStandingOrderId** | **String** | OB: Unique identification as assigned by the ASPSP to uniquely identify the domestic standing order resource. | 
**consentId** | **String** | OB: Unique identification as assigned by the ASPSP to uniquely identify the consent resource. | 
**creationDateTime** | [**OffsetDateTime**](OffsetDateTime.md) | Date and time at which the resource was created.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00 | 
**status** | [**StatusEnum**](#StatusEnum) | Specifies the status of the payment order resource. | 
**statusUpdateDateTime** | [**OffsetDateTime**](OffsetDateTime.md) | Date and time at which the resource status was updated.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00 | 
**refund** | [**OBWriteDomesticResponse5DataRefund**](OBWriteDomesticResponse5DataRefund.md) |  |  [optional]
**charges** | [**List&lt;OBWriteDomesticConsentResponse5DataCharges&gt;**](OBWriteDomesticConsentResponse5DataCharges.md) |  |  [optional]
**initiation** | [**OBWriteDomesticStandingOrder3DataInitiation**](OBWriteDomesticStandingOrder3DataInitiation.md) |  | 
**multiAuthorisation** | [**OBWriteDomesticResponse5DataMultiAuthorisation**](OBWriteDomesticResponse5DataMultiAuthorisation.md) |  |  [optional]
**debtor** | [**OBDebtorIdentification1**](OBDebtorIdentification1.md) |  |  [optional]

<a name="StatusEnum"></a>
## Enum: StatusEnum
Name | Value
---- | -----
CANCELLED | &quot;Cancelled&quot;
INITIATIONCOMPLETED | &quot;InitiationCompleted&quot;
INITIATIONFAILED | &quot;InitiationFailed&quot;
INITIATIONPENDING | &quot;InitiationPending&quot;
