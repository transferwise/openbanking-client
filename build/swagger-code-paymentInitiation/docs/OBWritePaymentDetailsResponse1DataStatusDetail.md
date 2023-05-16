# OBWritePaymentDetailsResponse1DataStatusDetail

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**localInstrument** | [**OBExternalLocalInstrument1Code**](OBExternalLocalInstrument1Code.md) |  |  [optional]
**status** | **String** | Status of a transfer, as assigned by the transaction administrator. | 
**statusReason** | [**StatusReasonEnum**](#StatusReasonEnum) | Reason Code provided for the status of a transfer. |  [optional]
**statusReasonDescription** | **String** | Reason provided for the status of a transfer. |  [optional]

<a name="StatusReasonEnum"></a>
## Enum: StatusReasonEnum
Name | Value
---- | -----
CANCELLED | &quot;Cancelled&quot;
PENDINGFAILINGSETTLEMENT | &quot;PendingFailingSettlement&quot;
PENDINGSETTLEMENT | &quot;PendingSettlement&quot;
PROPRIETARY | &quot;Proprietary&quot;
PROPRIETARYREJECTION | &quot;ProprietaryRejection&quot;
SUSPENDED | &quot;Suspended&quot;
UNMATCHED | &quot;Unmatched&quot;
