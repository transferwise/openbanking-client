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
 * OBWriteInternationalConsentResponse6
 */


public class OBWriteInternationalConsentResponse6 implements Serializable{
  private static final long serialVersionUID = 1L;
  @JsonProperty("Data")
  private OBWriteInternationalConsentResponse6Data data = null;

  @JsonProperty("Risk")
  private OBRisk1 risk = null;

  @JsonProperty("Links")
  private Links links = null;

  @JsonProperty("Meta")
  private Meta meta = null;

  public OBWriteInternationalConsentResponse6 data(OBWriteInternationalConsentResponse6Data data) {
    this.data = data;
    return this;
  }

   /**
   * Get data
   * @return data
  **/
  
  public OBWriteInternationalConsentResponse6Data getData() {
    return data;
  }

  public void setData(OBWriteInternationalConsentResponse6Data data) {
    this.data = data;
  }

  public OBWriteInternationalConsentResponse6 risk(OBRisk1 risk) {
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

  public OBWriteInternationalConsentResponse6 links(Links links) {
    this.links = links;
    return this;
  }

   /**
   * Get links
   * @return links
  **/
  
  public Links getLinks() {
    return links;
  }

  public void setLinks(Links links) {
    this.links = links;
  }

  public OBWriteInternationalConsentResponse6 meta(Meta meta) {
    this.meta = meta;
    return this;
  }

   /**
   * Get meta
   * @return meta
  **/
  
  public Meta getMeta() {
    return meta;
  }

  public void setMeta(Meta meta) {
    this.meta = meta;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OBWriteInternationalConsentResponse6 obWriteInternationalConsentResponse6 = (OBWriteInternationalConsentResponse6) o;
    return Objects.equals(this.data, obWriteInternationalConsentResponse6.data) &&
        Objects.equals(this.risk, obWriteInternationalConsentResponse6.risk) &&
        Objects.equals(this.links, obWriteInternationalConsentResponse6.links) &&
        Objects.equals(this.meta, obWriteInternationalConsentResponse6.meta);
  }

  @Override
  public int hashCode() {
    return Objects.hash(data, risk, links, meta);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBWriteInternationalConsentResponse6 {\n");
    
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
    sb.append("    risk: ").append(toIndentedString(risk)).append("\n");
    sb.append("    links: ").append(toIndentedString(links)).append("\n");
    sb.append("    meta: ").append(toIndentedString(meta)).append("\n");
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