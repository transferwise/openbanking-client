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
 * Financial institution servicing an account for the creditor.
 */


public class OBWriteInternational3DataInitiationCreditorAgent implements Serializable{
  private static final long serialVersionUID = 1L;
  @JsonProperty("SchemeName")
  private OBExternalFinancialInstitutionIdentification4Code schemeName = null;

  @JsonProperty("Identification")
  private String identification = null;

  @JsonProperty("Name")
  private String name = null;

  @JsonProperty("PostalAddress")
  private OBPostalAddress6 postalAddress = null;

  public OBWriteInternational3DataInitiationCreditorAgent schemeName(OBExternalFinancialInstitutionIdentification4Code schemeName) {
    this.schemeName = schemeName;
    return this;
  }

   /**
   * Get schemeName
   * @return schemeName
  **/
  
  public OBExternalFinancialInstitutionIdentification4Code getSchemeName() {
    return schemeName;
  }

  public void setSchemeName(OBExternalFinancialInstitutionIdentification4Code schemeName) {
    this.schemeName = schemeName;
  }

  public OBWriteInternational3DataInitiationCreditorAgent identification(String identification) {
    this.identification = identification;
    return this;
  }

   /**
   * Get identification
   * @return identification
  **/
  
  public String getIdentification() {
    return identification;
  }

  public void setIdentification(String identification) {
    this.identification = identification;
  }

  public OBWriteInternational3DataInitiationCreditorAgent name(String name) {
    this.name = name;
    return this;
  }

   /**
   * Get name
   * @return name
  **/
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public OBWriteInternational3DataInitiationCreditorAgent postalAddress(OBPostalAddress6 postalAddress) {
    this.postalAddress = postalAddress;
    return this;
  }

   /**
   * Get postalAddress
   * @return postalAddress
  **/
  
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
    OBWriteInternational3DataInitiationCreditorAgent obWriteInternational3DataInitiationCreditorAgent = (OBWriteInternational3DataInitiationCreditorAgent) o;
    return Objects.equals(this.schemeName, obWriteInternational3DataInitiationCreditorAgent.schemeName) &&
        Objects.equals(this.identification, obWriteInternational3DataInitiationCreditorAgent.identification) &&
        Objects.equals(this.name, obWriteInternational3DataInitiationCreditorAgent.name) &&
        Objects.equals(this.postalAddress, obWriteInternational3DataInitiationCreditorAgent.postalAddress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(schemeName, identification, name, postalAddress);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBWriteInternational3DataInitiationCreditorAgent {\n");
    
    sb.append("    schemeName: ").append(toIndentedString(schemeName)).append("\n");
    sb.append("    identification: ").append(toIndentedString(identification)).append("\n");
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