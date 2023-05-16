# OBWritePaymentDetailsResponse1DataPaymentStatus

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**paymentTransactionId** | **String** | Unique identifier for the transaction within an servicing institution. This identifier is both unique and immutable. | 
**status** | [**StatusEnum**](#StatusEnum) | Status of a transfe, as assigned by the transaction administrator. | 
**statusUpdateDateTime** | [**OffsetDateTime**](OffsetDateTime.md) | Date and time at which the status was assigned to the transfer.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00 | 
**statusDetail** | [**OBWritePaymentDetailsResponse1DataStatusDetail**](OBWritePaymentDetailsResponse1DataStatusDetail.md) |  |  [optional]

<a name="StatusEnum"></a>
## Enum: StatusEnum
Name | Value
---- | -----
ACCEPTED | &quot;Accepted&quot;
ACCEPTEDCANCELLATIONREQUEST | &quot;AcceptedCancellationRequest&quot;
ACCEPTEDCREDITSETTLEMENTCOMPLETED | &quot;AcceptedCreditSettlementCompleted&quot;
ACCEPTEDCUSTOMERPROFILE | &quot;AcceptedCustomerProfile&quot;
ACCEPTEDFUNDSCHECKED | &quot;AcceptedFundsChecked&quot;
ACCEPTEDSETTLEMENTCOMPLETED | &quot;AcceptedSettlementCompleted&quot;
ACCEPTEDSETTLEMENTINPROCESS | &quot;AcceptedSettlementInProcess&quot;
ACCEPTEDTECHNICALVALIDATION | &quot;AcceptedTechnicalValidation&quot;
ACCEPTEDWITHCHANGE | &quot;AcceptedWithChange&quot;
ACCEPTEDWITHOUTPOSTING | &quot;AcceptedWithoutPosting&quot;
CANCELLED | &quot;Cancelled&quot;
NOCANCELLATIONPROCESS | &quot;NoCancellationProcess&quot;
PARTIALLYACCEPTEDCANCELLATIONREQUEST | &quot;PartiallyAcceptedCancellationRequest&quot;
PARTIALLYACCEPTEDTECHNICALCORRECT | &quot;PartiallyAcceptedTechnicalCorrect&quot;
PAYMENTCANCELLED | &quot;PaymentCancelled&quot;
PENDING | &quot;Pending&quot;
PENDINGCANCELLATIONREQUEST | &quot;PendingCancellationRequest&quot;
RECEIVED | &quot;Received&quot;
REJECTED | &quot;Rejected&quot;
REJECTEDCANCELLATIONREQUEST | &quot;RejectedCancellationRequest&quot;
