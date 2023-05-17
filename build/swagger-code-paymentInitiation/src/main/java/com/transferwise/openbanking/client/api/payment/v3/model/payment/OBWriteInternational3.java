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
 * OBWriteInternational3
 */


public class OBWriteInternational3 implements Serializable{
  private static final long serialVersionUID = 1L;
  @JsonProperty("Data")
  private OBWriteInternational3Data data = null;

  @JsonProperty("Risk")
  private OBRisk1 risk = null;

  public OBWriteInternational3 data(OBWriteInternational3Data data) {
    this.data = data;
    return this;
  }

   /**
   * Get data
   * @return data
  **/
  
  public OBWriteInternational3Data getData() {
    return data;
  }

  public void setData(OBWriteInternational3Data data) {
    this.data = data;
  }

  public OBWriteInternational3 risk(OBRisk1 risk) {
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
    OBWriteInternational3 obWriteInternational3 = (OBWriteInternational3) o;
    return Objects.equals(this.data, obWriteInternational3.data) &&
        Objects.equals(this.risk, obWriteInternational3.risk);
  }

  @Override
  public int hashCode() {
    return Objects.hash(data, risk);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBWriteInternational3 {\n");
    
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