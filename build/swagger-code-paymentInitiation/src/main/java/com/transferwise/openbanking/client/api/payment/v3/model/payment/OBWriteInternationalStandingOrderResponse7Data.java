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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
/**
 * OBWriteInternationalStandingOrderResponse7Data
 */


public class OBWriteInternationalStandingOrderResponse7Data implements Serializable{
  private static final long serialVersionUID = 1L;
  @JsonProperty("InternationalStandingOrderId")
  private String internationalStandingOrderId = null;

  @JsonProperty("ConsentId")
  private String consentId = null;

  @JsonProperty("CreationDateTime")
  private OffsetDateTime creationDateTime = null;

  /**
   * Specifies the status of resource in code form.
   */
  public enum StatusEnum {
    CANCELLED("Cancelled"),
    INITIATIONCOMPLETED("InitiationCompleted"),
    INITIATIONFAILED("InitiationFailed"),
    INITIATIONPENDING("InitiationPending");

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

  @JsonProperty("Refund")
  private OBWriteInternationalStandingOrderResponse7DataRefund refund = null;

  @JsonProperty("Charges")
  private List<OBWriteDomesticConsentResponse5DataCharges> charges = null;

  @JsonProperty("Initiation")
  private OBWriteInternationalStandingOrderConsentResponse7DataInitiation initiation = null;

  @JsonProperty("MultiAuthorisation")
  private OBWriteDomesticResponse5DataMultiAuthorisation multiAuthorisation = null;

  @JsonProperty("Debtor")
  private OBCashAccountDebtor4 debtor = null;

  public OBWriteInternationalStandingOrderResponse7Data internationalStandingOrderId(String internationalStandingOrderId) {
    this.internationalStandingOrderId = internationalStandingOrderId;
    return this;
  }

   /**
   * OB: Unique identification as assigned by the ASPSP to uniquely identify the international standing order resource.
   * @return internationalStandingOrderId
  **/
  
  public String getInternationalStandingOrderId() {
    return internationalStandingOrderId;
  }

  public void setInternationalStandingOrderId(String internationalStandingOrderId) {
    this.internationalStandingOrderId = internationalStandingOrderId;
  }

  public OBWriteInternationalStandingOrderResponse7Data consentId(String consentId) {
    this.consentId = consentId;
    return this;
  }

   /**
   * OB: Unique identification as assigned by the ASPSP to uniquely identify the consent resource.
   * @return consentId
  **/
  
  public String getConsentId() {
    return consentId;
  }

  public void setConsentId(String consentId) {
    this.consentId = consentId;
  }

  public OBWriteInternationalStandingOrderResponse7Data creationDateTime(OffsetDateTime creationDateTime) {
    this.creationDateTime = creationDateTime;
    return this;
  }

   /**
   * Date and time at which the resource was created.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00
   * @return creationDateTime
  **/
  
  public OffsetDateTime getCreationDateTime() {
    return creationDateTime;
  }

  public void setCreationDateTime(OffsetDateTime creationDateTime) {
    this.creationDateTime = creationDateTime;
  }

  public OBWriteInternationalStandingOrderResponse7Data status(StatusEnum status) {
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

  public OBWriteInternationalStandingOrderResponse7Data statusUpdateDateTime(OffsetDateTime statusUpdateDateTime) {
    this.statusUpdateDateTime = statusUpdateDateTime;
    return this;
  }

   /**
   * Date and time at which the resource status was updated.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00
   * @return statusUpdateDateTime
  **/
  
  public OffsetDateTime getStatusUpdateDateTime() {
    return statusUpdateDateTime;
  }

  public void setStatusUpdateDateTime(OffsetDateTime statusUpdateDateTime) {
    this.statusUpdateDateTime = statusUpdateDateTime;
  }

  public OBWriteInternationalStandingOrderResponse7Data refund(OBWriteInternationalStandingOrderResponse7DataRefund refund) {
    this.refund = refund;
    return this;
  }

   /**
   * Get refund
   * @return refund
  **/
  
  public OBWriteInternationalStandingOrderResponse7DataRefund getRefund() {
    return refund;
  }

  public void setRefund(OBWriteInternationalStandingOrderResponse7DataRefund refund) {
    this.refund = refund;
  }

  public OBWriteInternationalStandingOrderResponse7Data charges(List<OBWriteDomesticConsentResponse5DataCharges> charges) {
    this.charges = charges;
    return this;
  }

  public OBWriteInternationalStandingOrderResponse7Data addChargesItem(OBWriteDomesticConsentResponse5DataCharges chargesItem) {
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
  
  public List<OBWriteDomesticConsentResponse5DataCharges> getCharges() {
    return charges;
  }

  public void setCharges(List<OBWriteDomesticConsentResponse5DataCharges> charges) {
    this.charges = charges;
  }

  public OBWriteInternationalStandingOrderResponse7Data initiation(OBWriteInternationalStandingOrderConsentResponse7DataInitiation initiation) {
    this.initiation = initiation;
    return this;
  }

   /**
   * Get initiation
   * @return initiation
  **/
  
  public OBWriteInternationalStandingOrderConsentResponse7DataInitiation getInitiation() {
    return initiation;
  }

  public void setInitiation(OBWriteInternationalStandingOrderConsentResponse7DataInitiation initiation) {
    this.initiation = initiation;
  }

  public OBWriteInternationalStandingOrderResponse7Data multiAuthorisation(OBWriteDomesticResponse5DataMultiAuthorisation multiAuthorisation) {
    this.multiAuthorisation = multiAuthorisation;
    return this;
  }

   /**
   * Get multiAuthorisation
   * @return multiAuthorisation
  **/
  
  public OBWriteDomesticResponse5DataMultiAuthorisation getMultiAuthorisation() {
    return multiAuthorisation;
  }

  public void setMultiAuthorisation(OBWriteDomesticResponse5DataMultiAuthorisation multiAuthorisation) {
    this.multiAuthorisation = multiAuthorisation;
  }

  public OBWriteInternationalStandingOrderResponse7Data debtor(OBCashAccountDebtor4 debtor) {
    this.debtor = debtor;
    return this;
  }

   /**
   * Get debtor
   * @return debtor
  **/
  
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
    OBWriteInternationalStandingOrderResponse7Data obWriteInternationalStandingOrderResponse7Data = (OBWriteInternationalStandingOrderResponse7Data) o;
    return Objects.equals(this.internationalStandingOrderId, obWriteInternationalStandingOrderResponse7Data.internationalStandingOrderId) &&
        Objects.equals(this.consentId, obWriteInternationalStandingOrderResponse7Data.consentId) &&
        Objects.equals(this.creationDateTime, obWriteInternationalStandingOrderResponse7Data.creationDateTime) &&
        Objects.equals(this.status, obWriteInternationalStandingOrderResponse7Data.status) &&
        Objects.equals(this.statusUpdateDateTime, obWriteInternationalStandingOrderResponse7Data.statusUpdateDateTime) &&
        Objects.equals(this.refund, obWriteInternationalStandingOrderResponse7Data.refund) &&
        Objects.equals(this.charges, obWriteInternationalStandingOrderResponse7Data.charges) &&
        Objects.equals(this.initiation, obWriteInternationalStandingOrderResponse7Data.initiation) &&
        Objects.equals(this.multiAuthorisation, obWriteInternationalStandingOrderResponse7Data.multiAuthorisation) &&
        Objects.equals(this.debtor, obWriteInternationalStandingOrderResponse7Data.debtor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(internationalStandingOrderId, consentId, creationDateTime, status, statusUpdateDateTime, refund, charges, initiation, multiAuthorisation, debtor);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBWriteInternationalStandingOrderResponse7Data {\n");
    
    sb.append("    internationalStandingOrderId: ").append(toIndentedString(internationalStandingOrderId)).append("\n");
    sb.append("    consentId: ").append(toIndentedString(consentId)).append("\n");
    sb.append("    creationDateTime: ").append(toIndentedString(creationDateTime)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    statusUpdateDateTime: ").append(toIndentedString(statusUpdateDateTime)).append("\n");
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