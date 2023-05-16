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
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
/**
 * Unambiguous identification of the refund account to which a refund will be made as a result of the transaction.
 */
@Schema(description = "Unambiguous identification of the refund account to which a refund will be made as a result of the transaction.")

public class OBWriteDomesticResponse5DataRefund implements Serializable{
  private static final long serialVersionUID = 1L;
  @JsonProperty("Account")
  private OBWriteDomesticResponse5DataRefundAccount account = null;

  public OBWriteDomesticResponse5DataRefund account(OBWriteDomesticResponse5DataRefundAccount account) {
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
    OBWriteDomesticResponse5DataRefund obWriteDomesticResponse5DataRefund = (OBWriteDomesticResponse5DataRefund) o;
    return Objects.equals(this.account, obWriteDomesticResponse5DataRefund.account);
  }

  @Override
  public int hashCode() {
    return Objects.hash(account);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBWriteDomesticResponse5DataRefund {\n");
    
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
