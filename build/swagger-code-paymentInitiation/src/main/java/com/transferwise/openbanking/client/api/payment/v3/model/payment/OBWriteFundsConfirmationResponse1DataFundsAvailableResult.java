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
import java.time.OffsetDateTime;
import java.io.Serializable;
/**
 * Result of a funds availability check.
 */


public class OBWriteFundsConfirmationResponse1DataFundsAvailableResult implements Serializable{
  private static final long serialVersionUID = 1L;
  @JsonProperty("FundsAvailableDateTime")
  private OffsetDateTime fundsAvailableDateTime = null;

  @JsonProperty("FundsAvailable")
  private Boolean fundsAvailable = null;

  public OBWriteFundsConfirmationResponse1DataFundsAvailableResult fundsAvailableDateTime(OffsetDateTime fundsAvailableDateTime) {
    this.fundsAvailableDateTime = fundsAvailableDateTime;
    return this;
  }

   /**
   * Date and time at which the funds availability check was generated.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00
   * @return fundsAvailableDateTime
  **/
  
  public OffsetDateTime getFundsAvailableDateTime() {
    return fundsAvailableDateTime;
  }

  public void setFundsAvailableDateTime(OffsetDateTime fundsAvailableDateTime) {
    this.fundsAvailableDateTime = fundsAvailableDateTime;
  }

  public OBWriteFundsConfirmationResponse1DataFundsAvailableResult fundsAvailable(Boolean fundsAvailable) {
    this.fundsAvailable = fundsAvailable;
    return this;
  }

   /**
   * Flag to indicate the availability of funds given the Amount in the consent request.
   * @return fundsAvailable
  **/
  
  public Boolean isFundsAvailable() {
    return fundsAvailable;
  }

  public void setFundsAvailable(Boolean fundsAvailable) {
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
    OBWriteFundsConfirmationResponse1DataFundsAvailableResult obWriteFundsConfirmationResponse1DataFundsAvailableResult = (OBWriteFundsConfirmationResponse1DataFundsAvailableResult) o;
    return Objects.equals(this.fundsAvailableDateTime, obWriteFundsConfirmationResponse1DataFundsAvailableResult.fundsAvailableDateTime) &&
        Objects.equals(this.fundsAvailable, obWriteFundsConfirmationResponse1DataFundsAvailableResult.fundsAvailable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fundsAvailableDateTime, fundsAvailable);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBWriteFundsConfirmationResponse1DataFundsAvailableResult {\n");
    
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