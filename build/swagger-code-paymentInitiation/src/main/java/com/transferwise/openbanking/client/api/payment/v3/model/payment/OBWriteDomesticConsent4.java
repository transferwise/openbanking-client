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
 * OBWriteDomesticConsent4
 */


public class OBWriteDomesticConsent4 implements Serializable{
  private static final long serialVersionUID = 1L;
  @JsonProperty("Data")
  private OBWriteDomesticConsent4Data data = null;

  @JsonProperty("Risk")
  private OBRisk1 risk = null;

  public OBWriteDomesticConsent4 data(OBWriteDomesticConsent4Data data) {
    this.data = data;
    return this;
  }

   /**
   * Get data
   * @return data
  **/
  
  public OBWriteDomesticConsent4Data getData() {
    return data;
  }

  public void setData(OBWriteDomesticConsent4Data data) {
    this.data = data;
  }

  public OBWriteDomesticConsent4 risk(OBRisk1 risk) {
    this.risk = risk;
    return this;
  }

   /**
   * Get risk
   * @return risk
  **/
  
  public OBRisk1 getRisk() {
    return risk;
  }

  public void setRisk(OBRisk1 risk) {
    this.risk = risk;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OBWriteDomesticConsent4 obWriteDomesticConsent4 = (OBWriteDomesticConsent4) o;
    return Objects.equals(this.data, obWriteDomesticConsent4.data) &&
        Objects.equals(this.risk, obWriteDomesticConsent4.risk);
  }

  @Override
  public int hashCode() {
    return Objects.hash(data, risk);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBWriteDomesticConsent4 {\n");
    
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
    sb.append("    risk: ").append(toIndentedString(risk)).append("\n");
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