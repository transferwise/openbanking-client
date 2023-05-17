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
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.io.Serializable;
/**
 * OBWriteInternationalScheduledConsent5Data
 */


public class OBWriteInternationalScheduledConsent5Data implements Serializable{
  private static final long serialVersionUID = 1L;
  /**
   * Specifies the Open Banking service request types.
   */
  public enum PermissionEnum {
    CREATE("Create");

    private String value;

    PermissionEnum(String value) {
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
    public static PermissionEnum fromValue(String input) {
      for (PermissionEnum b : PermissionEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("Permission")
  private PermissionEnum permission = null;

  /**
   * Specifies to share the refund account details with PISP
   */
  public enum ReadRefundAccountEnum {
    NO("No"),
    YES("Yes");

    private String value;

    ReadRefundAccountEnum(String value) {
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
    public static ReadRefundAccountEnum fromValue(String input) {
      for (ReadRefundAccountEnum b : ReadRefundAccountEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("ReadRefundAccount")
  private ReadRefundAccountEnum readRefundAccount = null;

  @JsonProperty("Initiation")
  private OBWriteInternationalScheduled3DataInitiation initiation = null;

  @JsonProperty("Authorisation")
  private OBWriteDomesticConsent4DataAuthorisation authorisation = null;

  @JsonProperty("SCASupportData")
  private OBSCASupportData1 scASupportData = null;

  public OBWriteInternationalScheduledConsent5Data permission(PermissionEnum permission) {
    this.permission = permission;
    return this;
  }

   /**
   * Specifies the Open Banking service request types.
   * @return permission
  **/
  
  public PermissionEnum getPermission() {
    return permission;
  }

  public void setPermission(PermissionEnum permission) {
    this.permission = permission;
  }

  public OBWriteInternationalScheduledConsent5Data readRefundAccount(ReadRefundAccountEnum readRefundAccount) {
    this.readRefundAccount = readRefundAccount;
    return this;
  }

   /**
   * Specifies to share the refund account details with PISP
   * @return readRefundAccount
  **/
  
  public ReadRefundAccountEnum getReadRefundAccount() {
    return readRefundAccount;
  }

  public void setReadRefundAccount(ReadRefundAccountEnum readRefundAccount) {
    this.readRefundAccount = readRefundAccount;
  }

  public OBWriteInternationalScheduledConsent5Data initiation(OBWriteInternationalScheduled3DataInitiation initiation) {
    this.initiation = initiation;
    return this;
  }

   /**
   * Get initiation
   * @return initiation
  **/
  
  public OBWriteInternationalScheduled3DataInitiation getInitiation() {
    return initiation;
  }

  public void setInitiation(OBWriteInternationalScheduled3DataInitiation initiation) {
    this.initiation = initiation;
  }

  public OBWriteInternationalScheduledConsent5Data authorisation(OBWriteDomesticConsent4DataAuthorisation authorisation) {
    this.authorisation = authorisation;
    return this;
  }

   /**
   * Get authorisation
   * @return authorisation
  **/
  
  public OBWriteDomesticConsent4DataAuthorisation getAuthorisation() {
    return authorisation;
  }

  public void setAuthorisation(OBWriteDomesticConsent4DataAuthorisation authorisation) {
    this.authorisation = authorisation;
  }

  public OBWriteInternationalScheduledConsent5Data scASupportData(OBSCASupportData1 scASupportData) {
    this.scASupportData = scASupportData;
    return this;
  }

   /**
   * Get scASupportData
   * @return scASupportData
  **/
  
  public OBSCASupportData1 getScASupportData() {
    return scASupportData;
  }

  public void setScASupportData(OBSCASupportData1 scASupportData) {
    this.scASupportData = scASupportData;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OBWriteInternationalScheduledConsent5Data obWriteInternationalScheduledConsent5Data = (OBWriteInternationalScheduledConsent5Data) o;
    return Objects.equals(this.permission, obWriteInternationalScheduledConsent5Data.permission) &&
        Objects.equals(this.readRefundAccount, obWriteInternationalScheduledConsent5Data.readRefundAccount) &&
        Objects.equals(this.initiation, obWriteInternationalScheduledConsent5Data.initiation) &&
        Objects.equals(this.authorisation, obWriteInternationalScheduledConsent5Data.authorisation) &&
        Objects.equals(this.scASupportData, obWriteInternationalScheduledConsent5Data.scASupportData);
  }

  @Override
  public int hashCode() {
    return Objects.hash(permission, readRefundAccount, initiation, authorisation, scASupportData);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBWriteInternationalScheduledConsent5Data {\n");
    
    sb.append("    permission: ").append(toIndentedString(permission)).append("\n");
    sb.append("    readRefundAccount: ").append(toIndentedString(readRefundAccount)).append("\n");
    sb.append("    initiation: ").append(toIndentedString(initiation)).append("\n");
    sb.append("    authorisation: ").append(toIndentedString(authorisation)).append("\n");
    sb.append("    scASupportData: ").append(toIndentedString(scASupportData)).append("\n");
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