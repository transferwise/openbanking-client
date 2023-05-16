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
import java.util.Arrays;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Name of the identification scheme, in a coded form as published in an external list.
 */
public enum OBExternalFinancialInstitutionIdentification4Code {
  UK_OBIE_BICFI("UK.OBIE.BICFI");

  private String value;

  OBExternalFinancialInstitutionIdentification4Code(String value) {
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
  public static OBExternalFinancialInstitutionIdentification4Code fromValue(String input) {
    for (OBExternalFinancialInstitutionIdentification4Code b : OBExternalFinancialInstitutionIdentification4Code.values()) {
      if (b.value.equals(input)) {
        return b;
      }
    }
    return null;
  }
}
