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
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomesticStandingOrder3DataInitiation;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
/**
 * OBWriteDomesticStandingOrder3Data
 */


public class OBWriteDomesticStandingOrder3Data implements Serializable{
  private static final long serialVersionUID = 1L;
  @JsonProperty("ConsentId")
  private String consentId = null;

  @JsonProperty("Initiation")
  private OBWriteDomesticStandingOrder3DataInitiation initiation = null;

  public OBWriteDomesticStandingOrder3Data consentId(String consentId) {
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

  public OBWriteDomesticStandingOrder3Data initiation(OBWriteDomesticStandingOrder3DataInitiation initiation) {
    this.initiation = initiation;
    return this;
  }

   /**
   * Get initiation
   * @return initiation
  **/
  @Schema(required = true, description = "")
  public OBWriteDomesticStandingOrder3DataInitiation getInitiation() {
    return initiation;
  }

  public void setInitiation(OBWriteDomesticStandingOrder3DataInitiation initiation) {
    this.initiation = initiation;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OBWriteDomesticStandingOrder3Data obWriteDomesticStandingOrder3Data = (OBWriteDomesticStandingOrder3Data) o;
    return Objects.equals(this.consentId, obWriteDomesticStandingOrder3Data.consentId) &&
        Objects.equals(this.initiation, obWriteDomesticStandingOrder3Data.initiation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(consentId, initiation);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBWriteDomesticStandingOrder3Data {\n");
    
    sb.append("    consentId: ").append(toIndentedString(consentId)).append("\n");
    sb.append("    initiation: ").append(toIndentedString(initiation)).append("\n");
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
