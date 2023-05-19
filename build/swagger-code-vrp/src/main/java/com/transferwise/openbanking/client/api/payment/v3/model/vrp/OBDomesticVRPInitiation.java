/*
 * OBIE VRP Profile
 * VRP OpenAPI Specification
 *
 * OpenAPI spec version: 3.1.9
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
 * OBDomesticVRPInitiation
 */

public class OBDomesticVRPInitiation implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("DebtorAccount")
  private OBCashAccountDebtorWithName debtorAccount = null;

  @JsonProperty("CreditorAccount")
  private OBCashAccountCreditor3 creditorAccount = null;

  @JsonProperty("CreditorPostalAddress")
  private OBPostalAddress6 creditorPostalAddress = null;

  @JsonProperty("RemittanceInformation")
  private OBDomesticVRPInitiationRemittanceInformation remittanceInformation = null;

  public OBDomesticVRPInitiation debtorAccount(OBCashAccountDebtorWithName debtorAccount) {
    this.debtorAccount = debtorAccount;
    return this;
  }

   /**
   * Get debtorAccount
   * @return debtorAccount
  **/
  
  public OBCashAccountDebtorWithName getDebtorAccount() {
    return debtorAccount;
  }

  public void setDebtorAccount(OBCashAccountDebtorWithName debtorAccount) {
    this.debtorAccount = debtorAccount;
  }

  public OBDomesticVRPInitiation creditorAccount(OBCashAccountCreditor3 creditorAccount) {
    this.creditorAccount = creditorAccount;
    return this;
  }

   /**
   * Get creditorAccount
   * @return creditorAccount
  **/
  
  public OBCashAccountCreditor3 getCreditorAccount() {
    return creditorAccount;
  }

  public void setCreditorAccount(OBCashAccountCreditor3 creditorAccount) {
    this.creditorAccount = creditorAccount;
  }

  public OBDomesticVRPInitiation creditorPostalAddress(OBPostalAddress6 creditorPostalAddress) {
    this.creditorPostalAddress = creditorPostalAddress;
    return this;
  }

   /**
   * Get creditorPostalAddress
   * @return creditorPostalAddress
  **/
  
  public OBPostalAddress6 getCreditorPostalAddress() {
    return creditorPostalAddress;
  }

  public void setCreditorPostalAddress(OBPostalAddress6 creditorPostalAddress) {
    this.creditorPostalAddress = creditorPostalAddress;
  }

  public OBDomesticVRPInitiation remittanceInformation(OBDomesticVRPInitiationRemittanceInformation remittanceInformation) {
    this.remittanceInformation = remittanceInformation;
    return this;
  }

   /**
   * Get remittanceInformation
   * @return remittanceInformation
  **/
  
  public OBDomesticVRPInitiationRemittanceInformation getRemittanceInformation() {
    return remittanceInformation;
  }

  public void setRemittanceInformation(OBDomesticVRPInitiationRemittanceInformation remittanceInformation) {
    this.remittanceInformation = remittanceInformation;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OBDomesticVRPInitiation obDomesticVRPInitiation = (OBDomesticVRPInitiation) o;
    return Objects.equals(this.debtorAccount, obDomesticVRPInitiation.debtorAccount) &&
        Objects.equals(this.creditorAccount, obDomesticVRPInitiation.creditorAccount) &&
        Objects.equals(this.creditorPostalAddress, obDomesticVRPInitiation.creditorPostalAddress) &&
        Objects.equals(this.remittanceInformation, obDomesticVRPInitiation.remittanceInformation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(debtorAccount, creditorAccount, creditorPostalAddress, remittanceInformation);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBDomesticVRPInitiation {\n");
    
    sb.append("    debtorAccount: ").append(toIndentedString(debtorAccount)).append("\n");
    sb.append("    creditorAccount: ").append(toIndentedString(creditorAccount)).append("\n");
    sb.append("    creditorPostalAddress: ").append(toIndentedString(creditorPostalAddress)).append("\n");
    sb.append("    remittanceInformation: ").append(toIndentedString(remittanceInformation)).append("\n");
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

