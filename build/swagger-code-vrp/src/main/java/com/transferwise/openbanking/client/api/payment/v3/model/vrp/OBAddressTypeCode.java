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
 * Identifies the nature of the postal address.
 */
public enum OBAddressTypeCode {
  
  BUSINESS("Business"),
  
  CORRESPONDENCE("Correspondence"),
  
  DELIVERYTO("DeliveryTo"),
  
  MAILTO("MailTo"),
  
  POBOX("POBox"),
  
  POSTAL("Postal"),
  
  RESIDENTIAL("Residential"),
  
  STATEMENT("Statement");

  private String value;

  OBAddressTypeCode(String value) {
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
  public static OBAddressTypeCode fromValue(String text) {
    for (OBAddressTypeCode b : OBAddressTypeCode.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    throw new IllegalArgumentException("Unexpected value '" + text + "'");
  }
}

