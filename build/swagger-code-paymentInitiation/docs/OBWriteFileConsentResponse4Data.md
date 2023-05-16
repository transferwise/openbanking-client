# OBWriteFileConsentResponse4Data

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**consentId** | **String** | OB: Unique identification as assigned by the ASPSP to uniquely identify the consent resource. | 
**creationDateTime** | [**OffsetDateTime**](OffsetDateTime.md) | Date and time at which the resource was created.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00 | 
**status** | [**StatusEnum**](#StatusEnum) | Specifies the status of consent resource in code form. | 
**statusUpdateDateTime** | [**OffsetDateTime**](OffsetDateTime.md) | Date and time at which the consent resource status was updated.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00 | 
**cutOffDateTime** | [**OffsetDateTime**](OffsetDateTime.md) | Specified cut-off date and time for the payment consent.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00 |  [optional]
**charges** | [**List&lt;OBWriteDomesticConsentResponse5DataCharges&gt;**](OBWriteDomesticConsentResponse5DataCharges.md) |  |  [optional]
**initiation** | [**OBWriteFile2DataInitiation**](OBWriteFile2DataInitiation.md) |  | 
**authorisation** | [**OBWriteDomesticConsent4DataAuthorisation**](OBWriteDomesticConsent4DataAuthorisation.md) |  |  [optional]
**scASupportData** | [**OBSCASupportData1**](OBSCASupportData1.md) |  |  [optional]
**debtor** | [**OBCashAccountDebtor4**](OBCashAccountDebtor4.md) |  |  [optional]

<a name="StatusEnum"></a>
## Enum: StatusEnum
Name | Value
---- | -----
AUTHORISED | &quot;Authorised&quot;
AWAITINGAUTHORISATION | &quot;AwaitingAuthorisation&quot;
AWAITINGUPLOAD | &quot;AwaitingUpload&quot;
CONSUMED | &quot;Consumed&quot;
REJECTED | &quot;Rejected&quot;
