/*
 * OBIE VRP Profile
 * VRP OpenAPI Specification
 *
 * OpenAPI spec version: 3.1.10
 * Contact: ServiceDesk@openbanking.org.uk
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package com.transferwise.openbanking.client.api.payment.v3.model.vrp;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.io.Serializable;

/**
 * OBError1
 */

public class OBError1 implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * Low level textual error code, e.g., UK.OBIE.Field.Missing
   */
  public enum ErrorCodeEnum {
    FIELD_EXPECTED("UK.OBIE.Field.Expected"),
    
    FIELD_INVALID("UK.OBIE.Field.Invalid"),
    
    FIELD_INVALIDDATE("UK.OBIE.Field.InvalidDate"),
    
    FIELD_MISSING("UK.OBIE.Field.Missing"),
    
    FIELD_UNEXPECTED("UK.OBIE.Field.Unexpected"),
    
    HEADER_INVALID("UK.OBIE.Header.Invalid"),
    
    HEADER_MISSING("UK.OBIE.Header.Missing"),
    
    REAUTHENTICATE("UK.OBIE.Reauthenticate"),
    
    RESOURCE_CONSENTMISMATCH("UK.OBIE.Resource.ConsentMismatch"),
    
    RESOURCE_INVALIDCONSENTSTATUS("UK.OBIE.Resource.InvalidConsentStatus"),
    
    RESOURCE_INVALIDFORMAT("UK.OBIE.Resource.InvalidFormat"),
    
    RESOURCE_NOTFOUND("UK.OBIE.Resource.NotFound"),
    
    RULES_AFTERCUTOFFDATETIME("UK.OBIE.Rules.AfterCutOffDateTime"),
    
    RULES_DUPLICATEREFERENCE("UK.OBIE.Rules.DuplicateReference"),
    
    RULES_FAILSCONTROLPARAMETERS("UK.OBIE.Rules.FailsControlParameters"),
    
    SIGNATURE_INVALID("UK.OBIE.Signature.Invalid"),
    
    SIGNATURE_INVALIDCLAIM("UK.OBIE.Signature.InvalidClaim"),
    
    SIGNATURE_MALFORMED("UK.OBIE.Signature.Malformed"),
    
    SIGNATURE_MISSING("UK.OBIE.Signature.Missing"),
    
    SIGNATURE_MISSINGCLAIM("UK.OBIE.Signature.MissingClaim"),
    
    SIGNATURE_UNEXPECTED("UK.OBIE.Signature.Unexpected"),
    
    UNEXPECTEDERROR("UK.OBIE.UnexpectedError"),
    
    UNSUPPORTED_ACCOUNTIDENTIFIER("UK.OBIE.Unsupported.AccountIdentifier"),
    
    UNSUPPORTED_ACCOUNTSECONDARYIDENTIFIER("UK.OBIE.Unsupported.AccountSecondaryIdentifier"),
    
    UNSUPPORTED_CURRENCY("UK.OBIE.Unsupported.Currency"),
    
    UNSUPPORTED_FREQUENCY("UK.OBIE.Unsupported.Frequency"),
    
    UNSUPPORTED_LOCALINSTRUMENT("UK.OBIE.Unsupported.LocalInstrument"),
    
    UNSUPPORTED_SCHEME("UK.OBIE.Unsupported.Scheme"),
    
    RULES_RESOURCEALREADYEXISTS("UK.OBIE.Rules.ResourceAlreadyExists");

    private String value;

    ErrorCodeEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ErrorCodeEnum fromValue(String text) {
      for (ErrorCodeEnum b : ErrorCodeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + text + "'");
    }
  }

  @JsonProperty("ErrorCode")
  private ErrorCodeEnum errorCode;

  @JsonProperty("Message")
  private String message;

  @JsonProperty("Path")
  private String path;

  @JsonProperty("Url")
  private String url;

  public OBError1 errorCode(ErrorCodeEnum errorCode) {
    this.errorCode = errorCode;
    return this;
  }

   /**
   * Low level textual error code, e.g., UK.OBIE.Field.Missing
   * @return errorCode
  **/
  
  public ErrorCodeEnum getErrorCode() {
    return errorCode;
  }

  public void setErrorCode(ErrorCodeEnum errorCode) {
    this.errorCode = errorCode;
  }

  public OBError1 message(String message) {
    this.message = message;
    return this;
  }

   /**
   * A description of the error that occurred. e.g., &#39;A mandatory field isn&#39;t supplied&#39; or &#39;RequestedExecutionDateTime must be in future&#39; OBIE doesn&#39;t standardise this field
   * @return message
  **/
  
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public OBError1 path(String path) {
    this.path = path;
    return this;
  }

   /**
   * Recommended but optional reference to the JSON Path of the field with error, e.g., Data.Initiation.InstructedAmount.Currency
   * @return path
  **/
  
  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public OBError1 url(String url) {
    this.url = url;
    return this;
  }

   /**
   * URL to help remediate the problem, or provide more information, or to API Reference, or help etc
   * @return url
  **/
  
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OBError1 obError1 = (OBError1) o;
    return Objects.equals(this.errorCode, obError1.errorCode) &&
        Objects.equals(this.message, obError1.message) &&
        Objects.equals(this.path, obError1.path) &&
        Objects.equals(this.url, obError1.url);
  }

  @Override
  public int hashCode() {
    return Objects.hash(errorCode, message, path, url);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBError1 {\n");
    
    sb.append("    errorCode: ").append(toIndentedString(errorCode)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    path: ").append(toIndentedString(path)).append("\n");
    sb.append("    url: ").append(toIndentedString(url)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

