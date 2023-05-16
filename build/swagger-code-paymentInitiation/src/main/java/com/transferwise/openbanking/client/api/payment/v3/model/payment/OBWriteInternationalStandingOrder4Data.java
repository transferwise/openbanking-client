/*
 * Payment Initiation API
 * Swagger for Payment Initiation API Specification
 *
 * OpenAPI spec version: v3.1.6
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
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteInternationalStandingOrder4DataInitiation;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
/**
 * OBWriteInternationalStandingOrder4Data
 */


public class OBWriteInternationalStandingOrder4Data implements Serializable{
  private static final long serialVersionUID = 1L;
  @JsonProperty("ConsentId")
  private String consentId = null;

  @JsonProperty("Initiation")
  private OBWriteInternationalStandingOrder4DataInitiation initiation = null;

  public OBWriteInternationalStandingOrder4Data consentId(String consentId) {
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

  public OBWriteInternationalStandingOrder4Data initiation(OBWriteInternationalStandingOrder4DataInitiation initiation) {
    this.initiation = initiation;
    return this;
  }

   /**
   * Get initiation
   * @return initiation
  **/
  @Schema(required = true, description = "")
  public OBWriteInternationalStandingOrder4DataInitiation getInitiation() {
    return initiation;
  }

  public void setInitiation(OBWriteInternationalStandingOrder4DataInitiation initiation) {
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
    OBWriteInternationalStandingOrder4Data obWriteInternationalStandingOrder4Data = (OBWriteInternationalStandingOrder4Data) o;
    return Objects.equals(this.consentId, obWriteInternationalStandingOrder4Data.consentId) &&
        Objects.equals(this.initiation, obWriteInternationalStandingOrder4Data.initiation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(consentId, initiation);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBWriteInternationalStandingOrder4Data {\n");
    
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
