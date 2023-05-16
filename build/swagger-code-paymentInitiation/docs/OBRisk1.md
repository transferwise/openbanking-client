# OBRisk1

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**paymentContextCode** | [**PaymentContextCodeEnum**](#PaymentContextCodeEnum) | Specifies the payment context * BillPayment - @deprecated * EcommerceGoods - @deprecated * EcommerceServices - @deprecated * Other - @deprecated * PartyToParty - @deprecated  |  [optional]
**merchantCategoryCode** | **String** | Category code conform to ISO 18245, related to the type of services or goods the merchant provides for the transaction. |  [optional]
**merchantCustomerIdentification** | **String** | The unique customer identifier of the PSU with the merchant. |  [optional]
**contractPresentInidicator** | **Boolean** | Indicates if Payee has a contractual relationship with the PISP. |  [optional]
**beneficiaryPrepopulatedIndicator** | **Boolean** | Indicates if PISP has immutably prepopulated payment details in for the PSU. |  [optional]
**paymentPurposeCode** | **String** | Category code, related to the type of services or goods that corresponds to the underlying purpose of the payment that conforms to Recommended UK Purpose Code in ISO 20022 Payment Messaging List |  [optional]
**beneficiaryAccountType** | [**OBExternalExtendedAccountType1Code**](OBExternalExtendedAccountType1Code.md) |  |  [optional]
**deliveryAddress** | [**OBRisk1DeliveryAddress**](OBRisk1DeliveryAddress.md) |  |  [optional]

<a name="PaymentContextCodeEnum"></a>
## Enum: PaymentContextCodeEnum
Name | Value
---- | -----
BILLINGGOODSANDSERVICESINADVANCE | &quot;BillingGoodsAndServicesInAdvance&quot;
BILLINGGOODSANDSERVICESINARREARS | &quot;BillingGoodsAndServicesInArrears&quot;
PISPPAYEE | &quot;PispPayee&quot;
ECOMMERCEMERCHANTINITIATEDPAYMENT | &quot;EcommerceMerchantInitiatedPayment&quot;
FACETOFACEPOINTOFSALE | &quot;FaceToFacePointOfSale&quot;
TRANSFERTOSELF | &quot;TransferToSelf&quot;
TRANSFERTOTHIRDPARTY | &quot;TransferToThirdParty&quot;
BILLPAYMENT | &quot;BillPayment&quot;
ECOMMERCEGOODS | &quot;EcommerceGoods&quot;
ECOMMERCESERVICES | &quot;EcommerceServices&quot;
OTHER | &quot;Other&quot;
PARTYTOPARTY | &quot;PartyToParty&quot;
