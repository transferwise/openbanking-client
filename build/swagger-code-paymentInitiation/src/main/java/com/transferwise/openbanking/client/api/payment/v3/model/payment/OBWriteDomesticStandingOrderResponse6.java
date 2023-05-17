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
 * OBWriteDomesticStandingOrderResponse6
 */


public class OBWriteDomesticStandingOrderResponse6 implements Serializable{
  private static final long serialVersionUID = 1L;
  @JsonProperty("Data")
  private OBWriteDomesticStandingOrderResponse6Data data = null;

  @JsonProperty("Links")
  private Links links = null;

  @JsonProperty("Meta")
  private Meta meta = null;

  public OBWriteDomesticStandingOrderResponse6 data(OBWriteDomesticStandingOrderResponse6Data data) {
    this.data = data;
    return this;
  }

   /**
   * Get data
   * @return data
  **/
  
  public OBWriteDomesticStandingOrderResponse6Data getData() {
    return data;
  }

  public void setData(OBWriteDomesticStandingOrderResponse6Data data) {
    this.data = data;
  }

  public OBWriteDomesticStandingOrderResponse6 links(Links links) {
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

  public OBWriteDomesticStandingOrderResponse6 meta(Meta meta) {
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
    OBWriteDomesticStandingOrderResponse6 obWriteDomesticStandingOrderResponse6 = (OBWriteDomesticStandingOrderResponse6) o;
    return Objects.equals(this.data, obWriteDomesticStandingOrderResponse6.data) &&
        Objects.equals(this.links, obWriteDomesticStandingOrderResponse6.links) &&
        Objects.equals(this.meta, obWriteDomesticStandingOrderResponse6.meta);
  }

  @Override
  public int hashCode() {
    return Objects.hash(data, links, meta);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBWriteDomesticStandingOrderResponse6 {\n");
    
    sb.append("    data: ").append(toIndentedString(data)).append("\n");
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
