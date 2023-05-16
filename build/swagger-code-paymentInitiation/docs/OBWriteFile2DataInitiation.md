# OBWriteFile2DataInitiation

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**fileType** | [**FileTypeEnum**](#FileTypeEnum) | Specifies the payment file type. | 
**fileHash** | **String** | A base64 encoding of a SHA256 hash of the file to be uploaded. | 
**fileReference** | **String** | Reference for the file. |  [optional]
**numberOfTransactions** | **String** | Number of individual transactions contained in the payment information group. |  [optional]
**controlSum** | [**BigDecimal**](BigDecimal.md) | Total of all individual amounts included in the group, irrespective of currencies. |  [optional]
**requestedExecutionDateTime** | [**OffsetDateTime**](OffsetDateTime.md) | Date at which the initiating party requests the clearing agent to process the payment.  Usage: This is the date on which the debtor&#x27;s account is to be debited.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00 |  [optional]
**localInstrument** | [**OBExternalLocalInstrument1Code**](OBExternalLocalInstrument1Code.md) |  |  [optional]
**debtorAccount** | [**OBWriteDomestic2DataInitiationDebtorAccount**](OBWriteDomestic2DataInitiationDebtorAccount.md) |  |  [optional]
**remittanceInformation** | [**OBWriteDomestic2DataInitiationRemittanceInformation**](OBWriteDomestic2DataInitiationRemittanceInformation.md) |  |  [optional]
**supplementaryData** | [**OBSupplementaryData1**](OBSupplementaryData1.md) |  |  [optional]

<a name="FileTypeEnum"></a>
## Enum: FileTypeEnum
Name | Value
---- | -----
PAYMENTINITIATION_3_1 | &quot;UK.OBIE.PaymentInitiation.3.1&quot;
PAIN_001_001_08 | &quot;UK.OBIE.pain.001.001.08&quot;
