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
import java.io.Serializable;

/**
 * OBDomesticVRPDetailsDataStatusDetail
 */

public class OBDomesticVRPDetailsDataStatusDetail implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("LocalInstrument")
  private OBExternalLocalInstrument1Code localInstrument = null;

  @JsonProperty("Status")
  private String status;

  /**
   * Reason Code provided for the status of a transfer.
   */
  public enum StatusReasonEnum {
    CANCELLED("Cancelled"),
    
    PENDINGFAILINGSETTLEMENT("PendingFailingSettlement"),
    
    PENDINGSETTLEMENT("PendingSettlement"),
    
    PROPRIETARY("Proprietary"),
    
    PROPRIETARYREJECTION("ProprietaryRejection"),
    
    SUSPENDED("Suspended"),
    
    UNMATCHED("Unmatched");

    private String value;

    StatusReasonEnum(String value) {
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
    public static StatusReasonEnum fromValue(String text) {
      for (StatusReasonEnum b : StatusReasonEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + text + "'");
    }
  }

  @JsonProperty("StatusReason")
  private StatusReasonEnum statusReason;

  @JsonProperty("StatusReasonDescription")
  private String statusReasonDescription;

  public OBDomesticVRPDetailsDataStatusDetail localInstrument(OBExternalLocalInstrument1Code localInstrument) {
    this.localInstrument = localInstrument;
    return this;
  }

   /**
   * Get localInstrument
   * @return localInstrument
  **/
  
  public OBExternalLocalInstrument1Code getLocalInstrument() {
    return localInstrument;
  }

  public void setLocalInstrument(OBExternalLocalInstrument1Code localInstrument) {
    this.localInstrument = localInstrument;
  }

  public OBDomesticVRPDetailsDataStatusDetail status(String status) {
    this.status = status;
    return this;
  }

   /**
   * Status of a transfer, as assigned by the transaction administrator.
   * @return status
  **/
  
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public OBDomesticVRPDetailsDataStatusDetail statusReason(StatusReasonEnum statusReason) {
    this.statusReason = statusReason;
    return this;
  }

   /**
   * Reason Code provided for the status of a transfer.
   * @return statusReason
  **/
  
  public StatusReasonEnum getStatusReason() {
    return statusReason;
  }

  public void setStatusReason(StatusReasonEnum statusReason) {
    this.statusReason = statusReason;
  }

  public OBDomesticVRPDetailsDataStatusDetail statusReasonDescription(String statusReasonDescription) {
    this.statusReasonDescription = statusReasonDescription;
    return this;
  }

   /**
   * Reason provided for the status of a transfer.
   * @return statusReasonDescription
  **/
  
  public String getStatusReasonDescription() {
    return statusReasonDescription;
  }

  public void setStatusReasonDescription(String statusReasonDescription) {
    this.statusReasonDescription = statusReasonDescription;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OBDomesticVRPDetailsDataStatusDetail obDomesticVRPDetailsDataStatusDetail = (OBDomesticVRPDetailsDataStatusDetail) o;
    return Objects.equals(this.localInstrument, obDomesticVRPDetailsDataStatusDetail.localInstrument) &&
        Objects.equals(this.status, obDomesticVRPDetailsDataStatusDetail.status) &&
        Objects.equals(this.statusReason, obDomesticVRPDetailsDataStatusDetail.statusReason) &&
        Objects.equals(this.statusReasonDescription, obDomesticVRPDetailsDataStatusDetail.statusReasonDescription);
  }

  @Override
  public int hashCode() {
    return Objects.hash(localInstrument, status, statusReason, statusReasonDescription);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBDomesticVRPDetailsDataStatusDetail {\n");
    
    sb.append("    localInstrument: ").append(toIndentedString(localInstrument)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    statusReason: ").append(toIndentedString(statusReason)).append("\n");
    sb.append("    statusReasonDescription: ").append(toIndentedString(statusReasonDescription)).append("\n");
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

