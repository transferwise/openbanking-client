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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * User community specific instrument. Usage: This element is used to specify a local instrument, local clearing option and/or further qualify the service or service level.
 */
public enum OBExternalLocalInstrument1Code {
  BACS("UK.OBIE.BACS"),
  BALANCETRANSFER("UK.OBIE.BalanceTransfer"),
  CHAPS("UK.OBIE.CHAPS"),
  EURO1("UK.OBIE.Euro1"),
  FPS("UK.OBIE.FPS"),
  LINK("UK.OBIE.Link"),
  MONEYTRANSFER("UK.OBIE.MoneyTransfer"),
  PAYM("UK.OBIE.Paym"),
  SEPACREDITTRANSFER("UK.OBIE.SEPACreditTransfer"),
  SEPAINSTANTCREDITTRANSFER("UK.OBIE.SEPAInstantCreditTransfer"),
  SWIFT("UK.OBIE.SWIFT"),
  TARGET2("UK.OBIE.Target2");

  private String value;

  OBExternalLocalInstrument1Code(String value) {
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
  public static OBExternalLocalInstrument1Code fromValue(String input) {
    for (OBExternalLocalInstrument1Code b : OBExternalLocalInstrument1Code.values()) {
      if (b.value.equals(input)) {
        return b;
      }
    }
    return null;
  }
}