# OBRisk1

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**paymentContextCode** | [**PaymentContextCodeEnum**](#PaymentContextCodeEnum) | Specifies the payment context |  [optional]
**merchantCategoryCode** | **String** | Category code conform to ISO 18245, related to the type of services or goods the merchant provides for the transaction. |  [optional]
**merchantCustomerIdentification** | **String** | The unique customer identifier of the PSU with the merchant. |  [optional]
**deliveryAddress** | [**OBRisk1DeliveryAddress**](OBRisk1DeliveryAddress.md) |  |  [optional]

<a name="PaymentContextCodeEnum"></a>
## Enum: PaymentContextCodeEnum
Name | Value
---- | -----
BILLPAYMENT | &quot;BillPayment&quot;
ECOMMERCEGOODS | &quot;EcommerceGoods&quot;
ECOMMERCESERVICES | &quot;EcommerceServices&quot;
OTHER | &quot;Other&quot;
PARTYTOPARTY | &quot;PartyToParty&quot;
