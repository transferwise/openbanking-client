# OBWriteDomesticScheduledConsent4Data

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**permission** | [**PermissionEnum**](#PermissionEnum) | Specifies the Open Banking service request types. | 
**readRefundAccount** | [**ReadRefundAccountEnum**](#ReadRefundAccountEnum) | Specifies to share the refund account details with PISP |  [optional]
**initiation** | [**OBWriteDomesticScheduled2DataInitiation**](OBWriteDomesticScheduled2DataInitiation.md) |  | 
**authorisation** | [**OBWriteDomesticConsent4DataAuthorisation**](OBWriteDomesticConsent4DataAuthorisation.md) |  |  [optional]
**scASupportData** | [**OBWriteDomesticConsent4DataSCASupportData**](OBWriteDomesticConsent4DataSCASupportData.md) |  |  [optional]

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
