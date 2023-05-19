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
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
/**
 * Set of elements used to identify a person or an organisation.
 */


public class OBDebtorIdentification1 implements Serializable{
  private static final long serialVersionUID = 1L;
  @JsonProperty("Name")
  private String name = null;

  public OBDebtorIdentification1 name(String name) {
    this.name = name;
    return this;
  }

   /**
   * The account name is the name or names of the account owner(s) represented at an account level, as displayed by the ASPSP&#x27;s online channels. Note, the account name is not the product name or the nickname of the account.
   * @return name
  **/
  
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OBDebtorIdentification1 obDebtorIdentification1 = (OBDebtorIdentification1) o;
    return Objects.equals(this.name, obDebtorIdentification1.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBDebtorIdentification1 {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
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
