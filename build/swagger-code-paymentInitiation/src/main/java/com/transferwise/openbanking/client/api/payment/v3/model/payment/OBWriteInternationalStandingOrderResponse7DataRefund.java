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
import java.util.Arrays;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomesticResponse5DataRefundAccount;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteInternationalResponse5DataRefundAgent;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteInternationalStandingOrderResponse7DataRefundCreditor;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
/**
 * OBWriteInternationalStandingOrderResponse7DataRefund
 */


public class OBWriteInternationalStandingOrderResponse7DataRefund implements Serializable{
  private static final long serialVersionUID = 1L;
  @JsonProperty("Creditor")
  private OBWriteInternationalStandingOrderResponse7DataRefundCreditor creditor = null;

  @JsonProperty("Agent")
  private OBWriteInternationalResponse5DataRefundAgent agent = null;

  @JsonProperty("Account")
  private OBWriteDomesticResponse5DataRefundAccount account = null;

  public OBWriteInternationalStandingOrderResponse7DataRefund creditor(OBWriteInternationalStandingOrderResponse7DataRefundCreditor creditor) {
    this.creditor = creditor;
    return this;
  }

   /**
   * Get creditor
   * @return creditor
  **/
  @Schema(description = "")
  public OBWriteInternationalStandingOrderResponse7DataRefundCreditor getCreditor() {
    return creditor;
  }

  public void setCreditor(OBWriteInternationalStandingOrderResponse7DataRefundCreditor creditor) {
    this.creditor = creditor;
  }

  public OBWriteInternationalStandingOrderResponse7DataRefund agent(OBWriteInternationalResponse5DataRefundAgent agent) {
    this.agent = agent;
    return this;
  }

   /**
   * Get agent
   * @return agent
  **/
  @Schema(description = "")
  public OBWriteInternationalResponse5DataRefundAgent getAgent() {
    return agent;
  }

  public void setAgent(OBWriteInternationalResponse5DataRefundAgent agent) {
    this.agent = agent;
  }

  public OBWriteInternationalStandingOrderResponse7DataRefund account(OBWriteDomesticResponse5DataRefundAccount account) {
    this.account = account;
    return this;
  }

   /**
   * Get account
   * @return account
  **/
  @Schema(required = true, description = "")
  public OBWriteDomesticResponse5DataRefundAccount getAccount() {
    return account;
  }

  public void setAccount(OBWriteDomesticResponse5DataRefundAccount account) {
    this.account = account;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OBWriteInternationalStandingOrderResponse7DataRefund obWriteInternationalStandingOrderResponse7DataRefund = (OBWriteInternationalStandingOrderResponse7DataRefund) o;
    return Objects.equals(this.creditor, obWriteInternationalStandingOrderResponse7DataRefund.creditor) &&
        Objects.equals(this.agent, obWriteInternationalStandingOrderResponse7DataRefund.agent) &&
        Objects.equals(this.account, obWriteInternationalStandingOrderResponse7DataRefund.account);
  }

  @Override
  public int hashCode() {
    return Objects.hash(creditor, agent, account);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBWriteInternationalStandingOrderResponse7DataRefund {\n");
    
    sb.append("    creditor: ").append(toIndentedString(creditor)).append("\n");
    sb.append("    agent: ").append(toIndentedString(agent)).append("\n");
    sb.append("    account: ").append(toIndentedString(account)).append("\n");
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
