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
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBPostalAddress6;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
/**
 * Set of elements used to identify a person or an organisation.
 */
@Schema(description = "Set of elements used to identify a person or an organisation.")

public class OBWriteInternationalResponse5DataRefundCreditor implements Serializable{
  private static final long serialVersionUID = 1L;
  @JsonProperty("Name")
  private String name = null;

  @JsonProperty("PostalAddress")
  private OBPostalAddress6 postalAddress = null;

  public OBWriteInternationalResponse5DataRefundCreditor name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Name by which a party is known and which is usually used to identify that party.
   * @return name
  **/
  @Schema(description = "Name by which a party is known and which is usually used to identify that party.")
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public OBWriteInternationalResponse5DataRefundCreditor postalAddress(OBPostalAddress6 postalAddress) {
    this.postalAddress = postalAddress;
    return this;
  }

   /**
   * Get postalAddress
   * @return postalAddress
  **/
  @Schema(description = "")
  public OBPostalAddress6 getPostalAddress() {
    return postalAddress;
  }

  public void setPostalAddress(OBPostalAddress6 postalAddress) {
    this.postalAddress = postalAddress;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OBWriteInternationalResponse5DataRefundCreditor obWriteInternationalResponse5DataRefundCreditor = (OBWriteInternationalResponse5DataRefundCreditor) o;
    return Objects.equals(this.name, obWriteInternationalResponse5DataRefundCreditor.name) &&
        Objects.equals(this.postalAddress, obWriteInternationalResponse5DataRefundCreditor.postalAddress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, postalAddress);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBWriteInternationalResponse5DataRefundCreditor {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    postalAddress: ").append(toIndentedString(postalAddress)).append("\n");
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
