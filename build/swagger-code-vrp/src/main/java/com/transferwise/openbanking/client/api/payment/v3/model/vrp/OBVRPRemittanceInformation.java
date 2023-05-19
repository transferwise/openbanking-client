/*
 * OBIE VRP Profile
 * VRP OpenAPI Specification
 *
 * OpenAPI spec version: 3.1.10
 * Contact: ServiceDesk@openbanking.org.uk
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package com.transferwise.openbanking.client.api.payment.v3.model.vrp;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

/**
 * Information supplied to enable the matching of an entry with the items that the transfer is intended to settle, such as commercial invoices in an accounts&#39; receivable system.
 */


public class OBVRPRemittanceInformation implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("Unstructured")
  private String unstructured;

  @JsonProperty("Reference")
  private String reference;

  public OBVRPRemittanceInformation unstructured(String unstructured) {
    this.unstructured = unstructured;
    return this;
  }

   /**
   * Information supplied to enable the matching/reconciliation of an entry with the items that the payment is intended to settle, such as commercial invoices in an accounts&#39; receivable system, in an unstructured form.
   * @return unstructured
  **/
  
  public String getUnstructured() {
    return unstructured;
  }

  public void setUnstructured(String unstructured) {
    this.unstructured = unstructured;
  }

  public OBVRPRemittanceInformation reference(String reference) {
    this.reference = reference;
    return this;
  }

   /**
   * Unique reference, as assigned by the creditor, to unambiguously refer to the payment transaction. The PISP must populate this with the same value as specified in the &#x60;Data.Initiation.RemittanceInformation.Reference&#x60; of the consent.
   * @return reference
  **/
  
  public String getReference() {
    return reference;
  }

  public void setReference(String reference) {
    this.reference = reference;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OBVRPRemittanceInformation obVRPRemittanceInformation = (OBVRPRemittanceInformation) o;
    return Objects.equals(this.unstructured, obVRPRemittanceInformation.unstructured) &&
        Objects.equals(this.reference, obVRPRemittanceInformation.reference);
  }

  @Override
  public int hashCode() {
    return Objects.hash(unstructured, reference);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBVRPRemittanceInformation {\n");
    
    sb.append("    unstructured: ").append(toIndentedString(unstructured)).append("\n");
    sb.append("    reference: ").append(toIndentedString(reference)).append("\n");
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

