# OBError1

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**errorCode** | [**ErrorCodeEnum**](#ErrorCodeEnum) | Low level textual error code, e.g., UK.OBIE.Field.Missing | 
**message** | **String** | A description of the error that occurred. e.g., &#x27;A mandatory field isn&#x27;t supplied&#x27; or &#x27;RequestedExecutionDateTime must be in future&#x27; OBIE doesn&#x27;t standardise this field | 
**path** | **String** | Recommended but optional reference to the JSON Path of the field with error, e.g., Data.Initiation.InstructedAmount.Currency |  [optional]
**url** | **String** | URL to help remediate the problem, or provide more information, or to API Reference, or help etc |  [optional]

<a name="ErrorCodeEnum"></a>
## Enum: ErrorCodeEnum
Name | Value
---- | -----
FIELD_EXPECTED | &quot;UK.OBIE.Field.Expected&quot;
FIELD_INVALID | &quot;UK.OBIE.Field.Invalid&quot;
FIELD_INVALIDDATE | &quot;UK.OBIE.Field.InvalidDate&quot;
FIELD_MISSING | &quot;UK.OBIE.Field.Missing&quot;
FIELD_UNEXPECTED | &quot;UK.OBIE.Field.Unexpected&quot;
HEADER_INVALID | &quot;UK.OBIE.Header.Invalid&quot;
HEADER_MISSING | &quot;UK.OBIE.Header.Missing&quot;
REAUTHENTICATE | &quot;UK.OBIE.Reauthenticate&quot;
RESOURCE_CONSENTMISMATCH | &quot;UK.OBIE.Resource.ConsentMismatch&quot;
RESOURCE_INVALIDCONSENTSTATUS | &quot;UK.OBIE.Resource.InvalidConsentStatus&quot;
RESOURCE_INVALIDFORMAT | &quot;UK.OBIE.Resource.InvalidFormat&quot;
RESOURCE_NOTFOUND | &quot;UK.OBIE.Resource.NotFound&quot;
RULES_AFTERCUTOFFDATETIME | &quot;UK.OBIE.Rules.AfterCutOffDateTime&quot;
RULES_DUPLICATEREFERENCE | &quot;UK.OBIE.Rules.DuplicateReference&quot;
SIGNATURE_INVALID | &quot;UK.OBIE.Signature.Invalid&quot;
SIGNATURE_INVALIDCLAIM | &quot;UK.OBIE.Signature.InvalidClaim&quot;
SIGNATURE_MALFORMED | &quot;UK.OBIE.Signature.Malformed&quot;
SIGNATURE_MISSING | &quot;UK.OBIE.Signature.Missing&quot;
SIGNATURE_MISSINGCLAIM | &quot;UK.OBIE.Signature.MissingClaim&quot;
SIGNATURE_UNEXPECTED | &quot;UK.OBIE.Signature.Unexpected&quot;
UNEXPECTEDERROR | &quot;UK.OBIE.UnexpectedError&quot;
UNSUPPORTED_ACCOUNTIDENTIFIER | &quot;UK.OBIE.Unsupported.AccountIdentifier&quot;
UNSUPPORTED_ACCOUNTSECONDARYIDENTIFIER | &quot;UK.OBIE.Unsupported.AccountSecondaryIdentifier&quot;
UNSUPPORTED_CURRENCY | &quot;UK.OBIE.Unsupported.Currency&quot;
UNSUPPORTED_FREQUENCY | &quot;UK.OBIE.Unsupported.Frequency&quot;
UNSUPPORTED_LOCALINSTRUMENT | &quot;UK.OBIE.Unsupported.LocalInstrument&quot;
UNSUPPORTED_SCHEME | &quot;UK.OBIE.Unsupported.Scheme&quot;
