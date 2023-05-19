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
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import java.io.Serializable;

/**
 * Availability result, clearly indicating the availability of funds given the Amount in the request.
 */


public class OBPAFundsAvailableResult1 implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("FundsAvailableDateTime")
  private OffsetDateTime fundsAvailableDateTime;

  /**
   * Availability result, clearly indicating the availability of funds given the Amount in the request.
   */
  public enum FundsAvailableEnum {
    AVAILABLE("Available"),
    
    NOTAVAILABLE("NotAvailable");

    private String value;

    FundsAvailableEnum(String value) {
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
    public static FundsAvailableEnum fromValue(String text) {
      for (FundsAvailableEnum b : FundsAvailableEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + text + "'");
    }
  }

  @JsonProperty("FundsAvailable")
  private FundsAvailableEnum fundsAvailable;

  public OBPAFundsAvailableResult1 fundsAvailableDateTime(OffsetDateTime fundsAvailableDateTime) {
    this.fundsAvailableDateTime = fundsAvailableDateTime;
    return this;
  }

   /**
   * Date and time at which the funds availability check was generated.
   * @return fundsAvailableDateTime
  **/
  
  public OffsetDateTime getFundsAvailableDateTime() {
    return fundsAvailableDateTime;
  }

  public void setFundsAvailableDateTime(OffsetDateTime fundsAvailableDateTime) {
    this.fundsAvailableDateTime = fundsAvailableDateTime;
  }

  public OBPAFundsAvailableResult1 fundsAvailable(FundsAvailableEnum fundsAvailable) {
    this.fundsAvailable = fundsAvailable;
    return this;
  }

   /**
   * Availability result, clearly indicating the availability of funds given the Amount in the request.
   * @return fundsAvailable
  **/
  
  public FundsAvailableEnum getFundsAvailable() {
    return fundsAvailable;
  }

  public void setFundsAvailable(FundsAvailableEnum fundsAvailable) {
    this.fundsAvailable = fundsAvailable;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OBPAFundsAvailableResult1 obPAFundsAvailableResult1 = (OBPAFundsAvailableResult1) o;
    return Objects.equals(this.fundsAvailableDateTime, obPAFundsAvailableResult1.fundsAvailableDateTime) &&
        Objects.equals(this.fundsAvailable, obPAFundsAvailableResult1.fundsAvailable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fundsAvailableDateTime, fundsAvailable);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBPAFundsAvailableResult1 {\n");
    
    sb.append("    fundsAvailableDateTime: ").append(toIndentedString(fundsAvailableDateTime)).append("\n");
    sb.append("    fundsAvailable: ").append(toIndentedString(fundsAvailable)).append("\n");
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

