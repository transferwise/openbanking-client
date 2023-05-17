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
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
/**
 * Identification assigned by an institution to identify an account. This identification is known by the account owner.
 */


public class OBWriteDomesticStandingOrder3DataInitiationCreditorAccount implements Serializable{
  private static final long serialVersionUID = 1L;
  @JsonProperty("SchemeName")
  private OBExternalAccountIdentification4Code schemeName = null;

  @JsonProperty("Identification")
  private String identification = null;

  @JsonProperty("Name")
  private String name = null;

  @JsonProperty("SecondaryIdentification")
  private String secondaryIdentification = null;

  public OBWriteDomesticStandingOrder3DataInitiationCreditorAccount schemeName(OBExternalAccountIdentification4Code schemeName) {
    this.schemeName = schemeName;
    return this;
  }

   /**
   * Get schemeName
   * @return schemeName
  **/
  
  public OBExternalAccountIdentification4Code getSchemeName() {
    return schemeName;
  }

  public void setSchemeName(OBExternalAccountIdentification4Code schemeName) {
    this.schemeName = schemeName;
  }

  public OBWriteDomesticStandingOrder3DataInitiationCreditorAccount identification(String identification) {
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

  public OBWriteDomesticStandingOrder3DataInitiationCreditorAccount name(String name) {
    this.name = name;
    return this;
  }

   /**
   * The account name is the name or names of the account owner(s) represented at an account level. Note, the account name is not the product name or the nickname of the account. OB: ASPSPs may carry out name validation for Confirmation of Payee, but it is not mandatory.
   * @return name
  **/
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public OBWriteDomesticStandingOrder3DataInitiationCreditorAccount secondaryIdentification(String secondaryIdentification) {
    this.secondaryIdentification = secondaryIdentification;
    return this;
  }

   /**
   * Get secondaryIdentification
   * @return secondaryIdentification
  **/
  
  public String getSecondaryIdentification() {
    return secondaryIdentification;
  }

  public void setSecondaryIdentification(String secondaryIdentification) {
    this.secondaryIdentification = secondaryIdentification;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OBWriteDomesticStandingOrder3DataInitiationCreditorAccount obWriteDomesticStandingOrder3DataInitiationCreditorAccount = (OBWriteDomesticStandingOrder3DataInitiationCreditorAccount) o;
    return Objects.equals(this.schemeName, obWriteDomesticStandingOrder3DataInitiationCreditorAccount.schemeName) &&
        Objects.equals(this.identification, obWriteDomesticStandingOrder3DataInitiationCreditorAccount.identification) &&
        Objects.equals(this.name, obWriteDomesticStandingOrder3DataInitiationCreditorAccount.name) &&
        Objects.equals(this.secondaryIdentification, obWriteDomesticStandingOrder3DataInitiationCreditorAccount.secondaryIdentification);
  }

  @Override
  public int hashCode() {
    return Objects.hash(schemeName, identification, name, secondaryIdentification);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBWriteDomesticStandingOrder3DataInitiationCreditorAccount {\n");
    
    sb.append("    schemeName: ").append(toIndentedString(schemeName)).append("\n");
    sb.append("    identification: ").append(toIndentedString(identification)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    secondaryIdentification: ").append(toIndentedString(secondaryIdentification)).append("\n");
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
