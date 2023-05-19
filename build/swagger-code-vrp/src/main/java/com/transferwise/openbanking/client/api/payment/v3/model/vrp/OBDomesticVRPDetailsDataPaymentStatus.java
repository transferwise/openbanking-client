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
 * OBDomesticVRPDetailsDataPaymentStatus
 */

public class OBDomesticVRPDetailsDataPaymentStatus implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("PaymentTransactionId")
  private String paymentTransactionId;

  /**
   * Status of a transfer, as assigned by the transaction administrator.
   */
  public enum StatusEnum {
    ACCEPTED("Accepted"),
    
    ACCEPTEDCANCELLATIONREQUEST("AcceptedCancellationRequest"),
    
    ACCEPTEDCREDITSETTLEMENTCOMPLETED("AcceptedCreditSettlementCompleted"),
    
    ACCEPTEDCUSTOMERPROFILE("AcceptedCustomerProfile"),
    
    ACCEPTEDFUNDSCHECKED("AcceptedFundsChecked"),
    
    ACCEPTEDSETTLEMENTCOMPLETED("AcceptedSettlementCompleted"),
    
    ACCEPTEDSETTLEMENTINPROCESS("AcceptedSettlementInProcess"),
    
    ACCEPTEDTECHNICALVALIDATION("AcceptedTechnicalValidation"),
    
    ACCEPTEDWITHCHANGE("AcceptedWithChange"),
    
    ACCEPTEDWITHOUTPOSTING("AcceptedWithoutPosting"),
    
    CANCELLED("Cancelled"),
    
    NOCANCELLATIONPROCESS("NoCancellationProcess"),
    
    PARTIALLYACCEPTEDCANCELLATIONREQUEST("PartiallyAcceptedCancellationRequest"),
    
    PARTIALLYACCEPTEDTECHNICALCORRECT("PartiallyAcceptedTechnicalCorrect"),
    
    PAYMENTCANCELLED("PaymentCancelled"),
    
    PENDING("Pending"),
    
    PENDINGCANCELLATIONREQUEST("PendingCancellationRequest"),
    
    RECEIVED("Received"),
    
    REJECTED("Rejected"),
    
    REJECTEDCANCELLATIONREQUEST("RejectedCancellationRequest");

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

  @JsonProperty("StatusDetail")
  private OBDomesticVRPDetailsDataStatusDetail statusDetail = null;

  public OBDomesticVRPDetailsDataPaymentStatus paymentTransactionId(String paymentTransactionId) {
    this.paymentTransactionId = paymentTransactionId;
    return this;
  }

   /**
   * Unique identifier for the transaction within an servicing institution. This identifier is both unique and immutable.
   * @return paymentTransactionId
  **/
  
  public String getPaymentTransactionId() {
    return paymentTransactionId;
  }

  public void setPaymentTransactionId(String paymentTransactionId) {
    this.paymentTransactionId = paymentTransactionId;
  }

  public OBDomesticVRPDetailsDataPaymentStatus status(StatusEnum status) {
    this.status = status;
    return this;
  }

   /**
   * Status of a transfer, as assigned by the transaction administrator.
   * @return status
  **/
  
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public OBDomesticVRPDetailsDataPaymentStatus statusUpdateDateTime(OffsetDateTime statusUpdateDateTime) {
    this.statusUpdateDateTime = statusUpdateDateTime;
    return this;
  }

   /**
   * Date and time at which the status was assigned to the transfer. 
   * @return statusUpdateDateTime
  **/
  
  public OffsetDateTime getStatusUpdateDateTime() {
    return statusUpdateDateTime;
  }

  public void setStatusUpdateDateTime(OffsetDateTime statusUpdateDateTime) {
    this.statusUpdateDateTime = statusUpdateDateTime;
  }

  public OBDomesticVRPDetailsDataPaymentStatus statusDetail(OBDomesticVRPDetailsDataStatusDetail statusDetail) {
    this.statusDetail = statusDetail;
    return this;
  }

   /**
   * Get statusDetail
   * @return statusDetail
  **/
  
  public OBDomesticVRPDetailsDataStatusDetail getStatusDetail() {
    return statusDetail;
  }

  public void setStatusDetail(OBDomesticVRPDetailsDataStatusDetail statusDetail) {
    this.statusDetail = statusDetail;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OBDomesticVRPDetailsDataPaymentStatus obDomesticVRPDetailsDataPaymentStatus = (OBDomesticVRPDetailsDataPaymentStatus) o;
    return Objects.equals(this.paymentTransactionId, obDomesticVRPDetailsDataPaymentStatus.paymentTransactionId) &&
        Objects.equals(this.status, obDomesticVRPDetailsDataPaymentStatus.status) &&
        Objects.equals(this.statusUpdateDateTime, obDomesticVRPDetailsDataPaymentStatus.statusUpdateDateTime) &&
        Objects.equals(this.statusDetail, obDomesticVRPDetailsDataPaymentStatus.statusDetail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(paymentTransactionId, status, statusUpdateDateTime, statusDetail);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBDomesticVRPDetailsDataPaymentStatus {\n");
    
    sb.append("    paymentTransactionId: ").append(toIndentedString(paymentTransactionId)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    statusUpdateDateTime: ").append(toIndentedString(statusUpdateDateTime)).append("\n");
    sb.append("    statusDetail: ").append(toIndentedString(statusDetail)).append("\n");
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

