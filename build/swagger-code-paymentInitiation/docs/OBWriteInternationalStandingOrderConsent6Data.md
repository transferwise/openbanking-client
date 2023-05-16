# OBWriteInternationalStandingOrderConsent6Data

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**permission** | [**PermissionEnum**](#PermissionEnum) | Specifies the Open Banking service request types. | 
**readRefundAccount** | [**ReadRefundAccountEnum**](#ReadRefundAccountEnum) | Specifies to share the refund account details with PISP |  [optional]
**initiation** | [**OBWriteInternationalStandingOrder4DataInitiation**](OBWriteInternationalStandingOrder4DataInitiation.md) |  | 
**authorisation** | [**OBWriteDomesticConsent4DataAuthorisation**](OBWriteDomesticConsent4DataAuthorisation.md) |  |  [optional]
**scASupportData** | [**OBSCASupportData1**](OBSCASupportData1.md) |  |  [optional]

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
