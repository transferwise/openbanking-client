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
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBCashAccountDebtor4;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomestic2DataInitiation;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomesticConsentResponse5DataCharges;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomesticResponse5DataMultiAuthorisation;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomesticResponse5DataRefund;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
/**
 * OBWriteDomesticResponse5Data
 */


public class OBWriteDomesticResponse5Data implements Serializable{
  private static final long serialVersionUID = 1L;
  @JsonProperty("DomesticPaymentId")
  private String domesticPaymentId = null;

  @JsonProperty("ConsentId")
  private String consentId = null;

  @JsonProperty("CreationDateTime")
  private OffsetDateTime creationDateTime = null;

  /**
   * Specifies the status of the payment information group.
   */
  public enum StatusEnum {
    ACCEPTEDCREDITSETTLEMENTCOMPLETED("AcceptedCreditSettlementCompleted"),
    ACCEPTEDSETTLEMENTCOMPLETED("AcceptedSettlementCompleted"),
    ACCEPTEDSETTLEMENTINPROCESS("AcceptedSettlementInProcess"),
    ACCEPTEDWITHOUTPOSTING("AcceptedWithoutPosting"),
    PENDING("Pending"),
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

  @JsonProperty("ExpectedExecutionDateTime")
  private OffsetDateTime expectedExecutionDateTime = null;

  @JsonProperty("ExpectedSettlementDateTime")
  private OffsetDateTime expectedSettlementDateTime = null;

  @JsonProperty("Refund")
  private OBWriteDomesticResponse5DataRefund refund = null;

  @JsonProperty("Charges")
  private List<OBWriteDomesticConsentResponse5DataCharges> charges = null;

  @JsonProperty("Initiation")
  private OBWriteDomestic2DataInitiation initiation = null;

  @JsonProperty("MultiAuthorisation")
  private OBWriteDomesticResponse5DataMultiAuthorisation multiAuthorisation = null;

  @JsonProperty("Debtor")
  private OBCashAccountDebtor4 debtor = null;

  public OBWriteDomesticResponse5Data domesticPaymentId(String domesticPaymentId) {
    this.domesticPaymentId = domesticPaymentId;
    return this;
  }

   /**
   * OB: Unique identification as assigned by the ASPSP to uniquely identify the domestic payment resource.
   * @return domesticPaymentId
  **/
  @Schema(required = true, description = "OB: Unique identification as assigned by the ASPSP to uniquely identify the domestic payment resource.")
  public String getDomesticPaymentId() {
    return domesticPaymentId;
  }

  public void setDomesticPaymentId(String domesticPaymentId) {
    this.domesticPaymentId = domesticPaymentId;
  }

  public OBWriteDomesticResponse5Data consentId(String consentId) {
    this.consentId = consentId;
    return this;
  }

   /**
   * OB: Unique identification as assigned by the ASPSP to uniquely identify the consent resource.
   * @return consentId
  **/
  @Schema(required = true, description = "OB: Unique identification as assigned by the ASPSP to uniquely identify the consent resource.")
  public String getConsentId() {
    return consentId;
  }

  public void setConsentId(String consentId) {
    this.consentId = consentId;
  }

  public OBWriteDomesticResponse5Data creationDateTime(OffsetDateTime creationDateTime) {
    this.creationDateTime = creationDateTime;
    return this;
  }

   /**
   * Date and time at which the message was created.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00
   * @return creationDateTime
  **/
  @Schema(required = true, description = "Date and time at which the message was created.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00")
  public OffsetDateTime getCreationDateTime() {
    return creationDateTime;
  }

  public void setCreationDateTime(OffsetDateTime creationDateTime) {
    this.creationDateTime = creationDateTime;
  }

  public OBWriteDomesticResponse5Data status(StatusEnum status) {
    this.status = status;
    return this;
  }

   /**
   * Specifies the status of the payment information group.
   * @return status
  **/
  @Schema(required = true, description = "Specifies the status of the payment information group.")
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public OBWriteDomesticResponse5Data statusUpdateDateTime(OffsetDateTime statusUpdateDateTime) {
    this.statusUpdateDateTime = statusUpdateDateTime;
    return this;
  }

   /**
   * Date and time at which the resource status was updated.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00
   * @return statusUpdateDateTime
  **/
  @Schema(required = true, description = "Date and time at which the resource status was updated.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00")
  public OffsetDateTime getStatusUpdateDateTime() {
    return statusUpdateDateTime;
  }

  public void setStatusUpdateDateTime(OffsetDateTime statusUpdateDateTime) {
    this.statusUpdateDateTime = statusUpdateDateTime;
  }

  public OBWriteDomesticResponse5Data expectedExecutionDateTime(OffsetDateTime expectedExecutionDateTime) {
    this.expectedExecutionDateTime = expectedExecutionDateTime;
    return this;
  }

   /**
   * Expected execution date and time for the payment resource.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00
   * @return expectedExecutionDateTime
  **/
  @Schema(description = "Expected execution date and time for the payment resource.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00")
  public OffsetDateTime getExpectedExecutionDateTime() {
    return expectedExecutionDateTime;
  }

  public void setExpectedExecutionDateTime(OffsetDateTime expectedExecutionDateTime) {
    this.expectedExecutionDateTime = expectedExecutionDateTime;
  }

  public OBWriteDomesticResponse5Data expectedSettlementDateTime(OffsetDateTime expectedSettlementDateTime) {
    this.expectedSettlementDateTime = expectedSettlementDateTime;
    return this;
  }

   /**
   * Expected settlement date and time for the payment resource.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00
   * @return expectedSettlementDateTime
  **/
  @Schema(description = "Expected settlement date and time for the payment resource.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00")
  public OffsetDateTime getExpectedSettlementDateTime() {
    return expectedSettlementDateTime;
  }

  public void setExpectedSettlementDateTime(OffsetDateTime expectedSettlementDateTime) {
    this.expectedSettlementDateTime = expectedSettlementDateTime;
  }

  public OBWriteDomesticResponse5Data refund(OBWriteDomesticResponse5DataRefund refund) {
    this.refund = refund;
    return this;
  }

   /**
   * Get refund
   * @return refund
  **/
  @Schema(description = "")
  public OBWriteDomesticResponse5DataRefund getRefund() {
    return refund;
  }

  public void setRefund(OBWriteDomesticResponse5DataRefund refund) {
    this.refund = refund;
  }

  public OBWriteDomesticResponse5Data charges(List<OBWriteDomesticConsentResponse5DataCharges> charges) {
    this.charges = charges;
    return this;
  }

  public OBWriteDomesticResponse5Data addChargesItem(OBWriteDomesticConsentResponse5DataCharges chargesItem) {
    if (this.charges == null) {
      this.charges = new ArrayList<>();
    }
    this.charges.add(chargesItem);
    return this;
  }

   /**
   * Get charges
   * @return charges
  **/
  @Schema(description = "")
  public List<OBWriteDomesticConsentResponse5DataCharges> getCharges() {
    return charges;
  }

  public void setCharges(List<OBWriteDomesticConsentResponse5DataCharges> charges) {
    this.charges = charges;
  }

  public OBWriteDomesticResponse5Data initiation(OBWriteDomestic2DataInitiation initiation) {
    this.initiation = initiation;
    return this;
  }

   /**
   * Get initiation
   * @return initiation
  **/
  @Schema(required = true, description = "")
  public OBWriteDomestic2DataInitiation getInitiation() {
    return initiation;
  }

  public void setInitiation(OBWriteDomestic2DataInitiation initiation) {
    this.initiation = initiation;
  }

  public OBWriteDomesticResponse5Data multiAuthorisation(OBWriteDomesticResponse5DataMultiAuthorisation multiAuthorisation) {
    this.multiAuthorisation = multiAuthorisation;
    return this;
  }

   /**
   * Get multiAuthorisation
   * @return multiAuthorisation
  **/
  @Schema(description = "")
  public OBWriteDomesticResponse5DataMultiAuthorisation getMultiAuthorisation() {
    return multiAuthorisation;
  }

  public void setMultiAuthorisation(OBWriteDomesticResponse5DataMultiAuthorisation multiAuthorisation) {
    this.multiAuthorisation = multiAuthorisation;
  }

  public OBWriteDomesticResponse5Data debtor(OBCashAccountDebtor4 debtor) {
    this.debtor = debtor;
    return this;
  }

   /**
   * Get debtor
   * @return debtor
  **/
  @Schema(description = "")
  public OBCashAccountDebtor4 getDebtor() {
    return debtor;
  }

  public void setDebtor(OBCashAccountDebtor4 debtor) {
    this.debtor = debtor;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OBWriteDomesticResponse5Data obWriteDomesticResponse5Data = (OBWriteDomesticResponse5Data) o;
    return Objects.equals(this.domesticPaymentId, obWriteDomesticResponse5Data.domesticPaymentId) &&
        Objects.equals(this.consentId, obWriteDomesticResponse5Data.consentId) &&
        Objects.equals(this.creationDateTime, obWriteDomesticResponse5Data.creationDateTime) &&
        Objects.equals(this.status, obWriteDomesticResponse5Data.status) &&
        Objects.equals(this.statusUpdateDateTime, obWriteDomesticResponse5Data.statusUpdateDateTime) &&
        Objects.equals(this.expectedExecutionDateTime, obWriteDomesticResponse5Data.expectedExecutionDateTime) &&
        Objects.equals(this.expectedSettlementDateTime, obWriteDomesticResponse5Data.expectedSettlementDateTime) &&
        Objects.equals(this.refund, obWriteDomesticResponse5Data.refund) &&
        Objects.equals(this.charges, obWriteDomesticResponse5Data.charges) &&
        Objects.equals(this.initiation, obWriteDomesticResponse5Data.initiation) &&
        Objects.equals(this.multiAuthorisation, obWriteDomesticResponse5Data.multiAuthorisation) &&
        Objects.equals(this.debtor, obWriteDomesticResponse5Data.debtor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(domesticPaymentId, consentId, creationDateTime, status, statusUpdateDateTime, expectedExecutionDateTime, expectedSettlementDateTime, refund, charges, initiation, multiAuthorisation, debtor);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBWriteDomesticResponse5Data {\n");
    
    sb.append("    domesticPaymentId: ").append(toIndentedString(domesticPaymentId)).append("\n");
    sb.append("    consentId: ").append(toIndentedString(consentId)).append("\n");
    sb.append("    creationDateTime: ").append(toIndentedString(creationDateTime)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    statusUpdateDateTime: ").append(toIndentedString(statusUpdateDateTime)).append("\n");
    sb.append("    expectedExecutionDateTime: ").append(toIndentedString(expectedExecutionDateTime)).append("\n");
    sb.append("    expectedSettlementDateTime: ").append(toIndentedString(expectedSettlementDateTime)).append("\n");
    sb.append("    refund: ").append(toIndentedString(refund)).append("\n");
    sb.append("    charges: ").append(toIndentedString(charges)).append("\n");
    sb.append("    initiation: ").append(toIndentedString(initiation)).append("\n");
    sb.append("    multiAuthorisation: ").append(toIndentedString(multiAuthorisation)).append("\n");
    sb.append("    debtor: ").append(toIndentedString(debtor)).append("\n");
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
