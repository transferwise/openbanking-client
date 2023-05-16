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
import com.transferwise.openbanking.client.api.payment.v3.model.payment.Links;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.Meta;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBRisk1;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteInternationalScheduledConsentResponse6Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
/**
 * OBWriteInternationalScheduledConsentResponse6
 */


public class OBWriteInternationalScheduledConsentResponse6 implements Serializable{
  private static final long serialVersionUID = 1L;
  @JsonProperty("Data")
  private OBWriteInternationalScheduledConsentResponse6Data data = null;

  @JsonProperty("Risk")
  private OBRisk1 risk = null;

  @JsonProperty("Links")
  private Links links = null;

  @JsonProperty("Meta")
  private Meta meta = null;

  public OBWriteInternationalScheduledConsentResponse6 data(OBWriteInternationalScheduledConsentResponse6Data data) {
    this.data = data;
    return this;
  }

   /**
   * Get data
   * @return data
  **/
  @Schema(required = true, description = "")
  public OBWriteInternationalScheduledConsentResponse6Data getData() {
    return data;
  }

  public void setData(OBWriteInternationalScheduledConsentResponse6Data data) {
    this.data = data;
  }

  public OBWriteInternationalScheduledConsentResponse6 risk(OBRisk1 risk) {
    this.risk = risk;
    return this;
  }

   /**
   * Get risk
   * @return risk
  **/
  @Schema(required = true, description = "")
  public OBRisk1 getRisk() {
    return risk;
  }

  public void setRisk(OBRisk1 risk) {
    this.risk = risk;
  }

  public OBWriteInternationalScheduledConsentResponse6 links(Links links) {
    this.links = links;
    return this;
  }

   /**
   * Get links
   * @return links
  **/
  @Schema(description = "")
  public Links getLinks() {
    return links;
  }

  public void setLinks(Links links) {
    this.links = links;
  }

  public OBWriteInternationalScheduledConsentResponse6 meta(Meta meta) {
    this.meta = meta;
    return this;
  }

   /**
   * Get meta
   * @return meta
  **/
  @Schema(description = "")
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
    OBWriteInternationalScheduledConsentResponse6 obWriteInternationalScheduledConsentResponse6 = (OBWriteInternationalScheduledConsentResponse6) o;
    return Objects.equals(this.data, obWriteInternationalScheduledConsentResponse6.data) &&
        Objects.equals(this.risk, obWriteInternationalScheduledConsentResponse6.risk) &&
        Objects.equals(this.links, obWriteInternationalScheduledConsentResponse6.links) &&
        Objects.equals(this.meta, obWriteInternationalScheduledConsentResponse6.meta);
  }

  @Override
  public int hashCode() {
    return Objects.hash(data, risk, links, meta);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBWriteInternationalScheduledConsentResponse6 {\n");
    
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
