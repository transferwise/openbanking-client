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


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Name of the identification scheme, in a coded form as published in an external list.
 */
public enum OBExternalAccountIdentification4Code {
  
  BBAN("UK.OBIE.BBAN"),
  
  IBAN("UK.OBIE.IBAN"),
  
  PAN("UK.OBIE.PAN"),
  
  PAYM("UK.OBIE.Paym"),
  
  SORTCODEACCOUNTNUMBER("UK.OBIE.SortCodeAccountNumber");

  private String value;

  OBExternalAccountIdentification4Code(String value) {
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
  public static OBExternalAccountIdentification4Code fromValue(String text) {
    for (OBExternalAccountIdentification4Code b : OBExternalAccountIdentification4Code.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + text + "'");
  }
}

