# OBWriteInternationalConsentResponse6DataExchangeRateInformation

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**unitCurrency** | **String** | Currency in which the rate of exchange is expressed in a currency exchange. In the example 1GBP &#x3D; xxxCUR, the unit currency is GBP. | 
**exchangeRate** | [**BigDecimal**](BigDecimal.md) | The factor used for conversion of an amount from one currency to another. This reflects the price at which one currency was bought with another currency. | 
**rateType** | [**RateTypeEnum**](#RateTypeEnum) | Specifies the type used to complete the currency exchange. | 
**contractIdentification** | **String** | Unique and unambiguous reference to the foreign exchange contract agreed between the initiating party/creditor and the debtor agent. |  [optional]
**expirationDateTime** | [**OffsetDateTime**](OffsetDateTime.md) | Specified date and time the exchange rate agreement will expire.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00 |  [optional]

<a name="RateTypeEnum"></a>
## Enum: RateTypeEnum
Name | Value
---- | -----
ACTUAL | &quot;Actual&quot;
AGREED | &quot;Agreed&quot;
INDICATIVE | &quot;Indicative&quot;
