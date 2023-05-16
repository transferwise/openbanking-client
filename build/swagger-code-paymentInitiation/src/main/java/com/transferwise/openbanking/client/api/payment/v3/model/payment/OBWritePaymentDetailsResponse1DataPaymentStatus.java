/*
 * Payment Initiation API
 * Swagger for Payment Initiation API Specification
 *
 * OpenAPI spec version: 3.1.10
 * Contact: ServiceDesk@openbanking.org.uk
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package com.transferwise.openbanking.client.api.payment.v3.model.payment;

import java.util.Objects;
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWritePaymentDetailsResponse1DataStatusDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.io.Serializable;
/**
 * Payment status details.
 */
@Schema(description = "Payment status details.")

public class OBWritePaymentDetailsResponse1DataPaymentStatus implements Serializable{
  private static final long serialVersionUID = 1L;
  @JsonProperty("PaymentTransactionId")
  private String paymentTransactionId = null;

  /**
   * Status of a transfe, as assigned by the transaction administrator.
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
    public static StatusEnum fromValue(String input) {
      for (StatusEnum b : StatusEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("Status")
  private StatusEnum status = null;

  @JsonProperty("StatusUpdateDateTime")
  private OffsetDateTime statusUpdateDateTime = null;

  @JsonProperty("StatusDetail")
  private OBWritePaymentDetailsResponse1DataStatusDetail statusDetail = null;

  public OBWritePaymentDetailsResponse1DataPaymentStatus paymentTransactionId(String paymentTransactionId) {
    this.paymentTransactionId = paymentTransactionId;
    return this;
  }

   /**
   * Unique identifier for the transaction within an servicing institution. This identifier is both unique and immutable.
   * @return paymentTransactionId
  **/
  @Schema(required = true, description = "Unique identifier for the transaction within an servicing institution. This identifier is both unique and immutable.")
  public String getPaymentTransactionId() {
    return paymentTransactionId;
  }

  public void setPaymentTransactionId(String paymentTransactionId) {
    this.paymentTransactionId = paymentTransactionId;
  }

  public OBWritePaymentDetailsResponse1DataPaymentStatus status(StatusEnum status) {
    this.status = status;
    return this;
  }

   /**
   * Status of a transfe, as assigned by the transaction administrator.
   * @return status
  **/
  @Schema(required = true, description = "Status of a transfe, as assigned by the transaction administrator.")
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public OBWritePaymentDetailsResponse1DataPaymentStatus statusUpdateDateTime(OffsetDateTime statusUpdateDateTime) {
    this.statusUpdateDateTime = statusUpdateDateTime;
    return this;
  }

   /**
   * Date and time at which the status was assigned to the transfer.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00
   * @return statusUpdateDateTime
  **/
  @Schema(required = true, description = "Date and time at which the status was assigned to the transfer.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00")
  public OffsetDateTime getStatusUpdateDateTime() {
    return statusUpdateDateTime;
  }

  public void setStatusUpdateDateTime(OffsetDateTime statusUpdateDateTime) {
    this.statusUpdateDateTime = statusUpdateDateTime;
  }

  public OBWritePaymentDetailsResponse1DataPaymentStatus statusDetail(OBWritePaymentDetailsResponse1DataStatusDetail statusDetail) {
    this.statusDetail = statusDetail;
    return this;
  }

   /**
   * Get statusDetail
   * @return statusDetail
  **/
  @Schema(description = "")
  public OBWritePaymentDetailsResponse1DataStatusDetail getStatusDetail() {
    return statusDetail;
  }

  public void setStatusDetail(OBWritePaymentDetailsResponse1DataStatusDetail statusDetail) {
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
    OBWritePaymentDetailsResponse1DataPaymentStatus obWritePaymentDetailsResponse1DataPaymentStatus = (OBWritePaymentDetailsResponse1DataPaymentStatus) o;
    return Objects.equals(this.paymentTransactionId, obWritePaymentDetailsResponse1DataPaymentStatus.paymentTransactionId) &&
        Objects.equals(this.status, obWritePaymentDetailsResponse1DataPaymentStatus.status) &&
        Objects.equals(this.statusUpdateDateTime, obWritePaymentDetailsResponse1DataPaymentStatus.statusUpdateDateTime) &&
        Objects.equals(this.statusDetail, obWritePaymentDetailsResponse1DataPaymentStatus.statusDetail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(paymentTransactionId, status, statusUpdateDateTime, statusDetail);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBWritePaymentDetailsResponse1DataPaymentStatus {\n");
    
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
