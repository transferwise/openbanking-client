/*
 * OBIE VRP Profile
 * VRP OpenAPI Specification
 *
 * OpenAPI spec version: 3.1.9
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
import java.time.OffsetDateTime;
import java.io.Serializable;

/**
 * OBDomesticVRPConsentResponseData
 */

public class OBDomesticVRPConsentResponseData implements Serializable {
  private static final long serialVersionUID = 1L;

  /**
   * Indicates whether information about RefundAccount should be included in the payment response. 
   */
  public enum ReadRefundAccountEnum {
    YES("Yes"),
    
    NO("No");

    private String value;

    ReadRefundAccountEnum(String value) {
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
    public static ReadRefundAccountEnum fromValue(String text) {
      for (ReadRefundAccountEnum b : ReadRefundAccountEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + text + "'");
    }
  }

  @JsonProperty("ReadRefundAccount")
  private ReadRefundAccountEnum readRefundAccount;

  @JsonProperty("ConsentId")
  private String consentId;

  @JsonProperty("CreationDateTime")
  private OffsetDateTime creationDateTime;

  /**
   * Specifies the status of resource in code form. 
   */
  public enum StatusEnum {
    AUTHORISED("Authorised"),
    
    AWAITINGAUTHORISATION("AwaitingAuthorisation"),
    
    REJECTED("Rejected");

    private String value;

    StatusEnum(String value) {
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
    public static StatusEnum fromValue(String text) {
      for (StatusEnum b : StatusEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + text + "'");
    }
  }

  @JsonProperty("Status")
  private StatusEnum status;

  @JsonProperty("StatusUpdateDateTime")
  private OffsetDateTime statusUpdateDateTime;

  @JsonProperty("ControlParameters")
  private OBDomesticVRPControlParameters controlParameters = null;

  @JsonProperty("Initiation")
  private OBDomesticVRPInitiation initiation = null;

  @JsonProperty("DebtorAccount")
  private OBCashAccountDebtorWithName debtorAccount = null;

  public OBDomesticVRPConsentResponseData readRefundAccount(ReadRefundAccountEnum readRefundAccount) {
    this.readRefundAccount = readRefundAccount;
    return this;
  }

   /**
   * Indicates whether information about RefundAccount should be included in the payment response. 
   * @return readRefundAccount
  **/
  
  public ReadRefundAccountEnum getReadRefundAccount() {
    return readRefundAccount;
  }

  public void setReadRefundAccount(ReadRefundAccountEnum readRefundAccount) {
    this.readRefundAccount = readRefundAccount;
  }

  public OBDomesticVRPConsentResponseData consentId(String consentId) {
    this.consentId = consentId;
    return this;
  }

   /**
   * Unique identification as assigned by the ASPSP to uniquely identify the consent resource. 
   * @return consentId
  **/
  
  public String getConsentId() {
    return consentId;
  }

  public void setConsentId(String consentId) {
    this.consentId = consentId;
  }

  public OBDomesticVRPConsentResponseData creationDateTime(OffsetDateTime creationDateTime) {
    this.creationDateTime = creationDateTime;
    return this;
  }

   /**
   * Date and time at which the resource was created. 
   * @return creationDateTime
  **/
  
  public OffsetDateTime getCreationDateTime() {
    return creationDateTime;
  }

  public void setCreationDateTime(OffsetDateTime creationDateTime) {
    this.creationDateTime = creationDateTime;
  }

  public OBDomesticVRPConsentResponseData status(StatusEnum status) {
    this.status = status;
    return this;
  }

   /**
   * Specifies the status of resource in code form. 
   * @return status
  **/
  
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public OBDomesticVRPConsentResponseData statusUpdateDateTime(OffsetDateTime statusUpdateDateTime) {
    this.statusUpdateDateTime = statusUpdateDateTime;
    return this;
  }

   /**
   * Date and time at which the resource status was updated. 
   * @return statusUpdateDateTime
  **/
  
  public OffsetDateTime getStatusUpdateDateTime() {
    return statusUpdateDateTime;
  }

  public void setStatusUpdateDateTime(OffsetDateTime statusUpdateDateTime) {
    this.statusUpdateDateTime = statusUpdateDateTime;
  }

  public OBDomesticVRPConsentResponseData controlParameters(OBDomesticVRPControlParameters controlParameters) {
    this.controlParameters = controlParameters;
    return this;
  }

   /**
   * Get controlParameters
   * @return controlParameters
  **/
  
  public OBDomesticVRPControlParameters getControlParameters() {
    return controlParameters;
  }

  public void setControlParameters(OBDomesticVRPControlParameters controlParameters) {
    this.controlParameters = controlParameters;
  }

  public OBDomesticVRPConsentResponseData initiation(OBDomesticVRPInitiation initiation) {
    this.initiation = initiation;
    return this;
  }

   /**
   * Get initiation
   * @return initiation
  **/
  
  public OBDomesticVRPInitiation getInitiation() {
    return initiation;
  }

  public void setInitiation(OBDomesticVRPInitiation initiation) {
    this.initiation = initiation;
  }

  public OBDomesticVRPConsentResponseData debtorAccount(OBCashAccountDebtorWithName debtorAccount) {
    this.debtorAccount = debtorAccount;
    return this;
  }

   /**
   * Get debtorAccount
   * @return debtorAccount
  **/
  
  public OBCashAccountDebtorWithName getDebtorAccount() {
    return debtorAccount;
  }

  public void setDebtorAccount(OBCashAccountDebtorWithName debtorAccount) {
    this.debtorAccount = debtorAccount;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OBDomesticVRPConsentResponseData obDomesticVRPConsentResponseData = (OBDomesticVRPConsentResponseData) o;
    return Objects.equals(this.readRefundAccount, obDomesticVRPConsentResponseData.readRefundAccount) &&
        Objects.equals(this.consentId, obDomesticVRPConsentResponseData.consentId) &&
        Objects.equals(this.creationDateTime, obDomesticVRPConsentResponseData.creationDateTime) &&
        Objects.equals(this.status, obDomesticVRPConsentResponseData.status) &&
        Objects.equals(this.statusUpdateDateTime, obDomesticVRPConsentResponseData.statusUpdateDateTime) &&
        Objects.equals(this.controlParameters, obDomesticVRPConsentResponseData.controlParameters) &&
        Objects.equals(this.initiation, obDomesticVRPConsentResponseData.initiation) &&
        Objects.equals(this.debtorAccount, obDomesticVRPConsentResponseData.debtorAccount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(readRefundAccount, consentId, creationDateTime, status, statusUpdateDateTime, controlParameters, initiation, debtorAccount);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBDomesticVRPConsentResponseData {\n");
    
    sb.append("    readRefundAccount: ").append(toIndentedString(readRefundAccount)).append("\n");
    sb.append("    consentId: ").append(toIndentedString(consentId)).append("\n");
    sb.append("    creationDateTime: ").append(toIndentedString(creationDateTime)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    statusUpdateDateTime: ").append(toIndentedString(statusUpdateDateTime)).append("\n");
    sb.append("    controlParameters: ").append(toIndentedString(controlParameters)).append("\n");
    sb.append("    initiation: ").append(toIndentedString(initiation)).append("\n");
    sb.append("    debtorAccount: ").append(toIndentedString(debtorAccount)).append("\n");
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

