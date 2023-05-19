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


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Specifies the extended type of account.
 */
public enum OBExternalExtendedAccountType1Code {
  
  BUSINESS("Business"),
  
  BUSINESSSAVINGSACCOUNT("BusinessSavingsAccount"),
  
  CHARITY("Charity"),
  
  COLLECTION("Collection"),
  
  CORPORATE("Corporate"),
  
  EWALLET("Ewallet"),
  
  GOVERNMENT("Government"),
  
  INVESTMENT("Investment"),
  
  ISA("ISA"),
  
  JOINTPERSONAL("JointPersonal"),
  
  PENSION("Pension"),
  
  PERSONAL("Personal"),
  
  PERSONALSAVINGSACCOUNT("PersonalSavingsAccount"),
  
  PREMIER("Premier"),
  
  WEALTH("Wealth");

  private String value;

  OBExternalExtendedAccountType1Code(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static OBExternalExtendedAccountType1Code fromValue(String text) {
    for (OBExternalExtendedAccountType1Code b : OBExternalExtendedAccountType1Code.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + text + "'");
  }
}

