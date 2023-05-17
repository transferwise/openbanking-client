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
import java.io.Serializable;
/**
 * Set of elements used to provide details of a charge for the payment initiation.
 */


public class OBWriteDomesticConsentResponse5DataCharges implements Serializable{
  private static final long serialVersionUID = 1L;
  @JsonProperty("ChargeBearer")
  private OBChargeBearerType1Code chargeBearer = null;

  @JsonProperty("Type")
  private OBExternalPaymentChargeType1Code type = null;

  @JsonProperty("Amount")
  private OBActiveOrHistoricCurrencyAndAmount amount = null;

  public OBWriteDomesticConsentResponse5DataCharges chargeBearer(OBChargeBearerType1Code chargeBearer) {
    this.chargeBearer = chargeBearer;
    return this;
  }

   /**
   * Get chargeBearer
   * @return chargeBearer
  **/
  
  public OBChargeBearerType1Code getChargeBearer() {
    return chargeBearer;
  }

  public void setChargeBearer(OBChargeBearerType1Code chargeBearer) {
    this.chargeBearer = chargeBearer;
  }

  public OBWriteDomesticConsentResponse5DataCharges type(OBExternalPaymentChargeType1Code type) {
    this.type = type;
    return this;
  }

   /**
   * Get type
   * @return type
  **/
  
  public OBExternalPaymentChargeType1Code getType() {
    return type;
  }

  public void setType(OBExternalPaymentChargeType1Code type) {
    this.type = type;
  }

  public OBWriteDomesticConsentResponse5DataCharges amount(OBActiveOrHistoricCurrencyAndAmount amount) {
    this.amount = amount;
    return this;
  }

   /**
   * Get amount
   * @return amount
  **/
  
  public OBActiveOrHistoricCurrencyAndAmount getAmount() {
    return amount;
  }

  public void setAmount(OBActiveOrHistoricCurrencyAndAmount amount) {
    this.amount = amount;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OBWriteDomesticConsentResponse5DataCharges obWriteDomesticConsentResponse5DataCharges = (OBWriteDomesticConsentResponse5DataCharges) o;
    return Objects.equals(this.chargeBearer, obWriteDomesticConsentResponse5DataCharges.chargeBearer) &&
        Objects.equals(this.type, obWriteDomesticConsentResponse5DataCharges.type) &&
        Objects.equals(this.amount, obWriteDomesticConsentResponse5DataCharges.amount);
  }

  @Override
  public int hashCode() {
    return Objects.hash(chargeBearer, type, amount);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBWriteDomesticConsentResponse5DataCharges {\n");
    
    sb.append("    chargeBearer: ").append(toIndentedString(chargeBearer)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    amount: ").append(toIndentedString(amount)).append("\n");
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