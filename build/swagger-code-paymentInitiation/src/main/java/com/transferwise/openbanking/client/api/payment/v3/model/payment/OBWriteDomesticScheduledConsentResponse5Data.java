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
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
/**
 * OBWriteDomesticScheduledConsentResponse5Data
 */


public class OBWriteDomesticScheduledConsentResponse5Data implements Serializable{
  private static final long serialVersionUID = 1L;
  @JsonProperty("ConsentId")
  private String consentId = null;

  @JsonProperty("CreationDateTime")
  private OffsetDateTime creationDateTime = null;

  /**
   * Specifies the status of consent resource in code form.
   */
  public enum StatusEnum {
    AUTHORISED("Authorised"),
    AWAITINGAUTHORISATION("AwaitingAuthorisation"),
    CONSUMED("Consumed"),
    REJECTED("Rejected");

    private String value;

    StatusEnum(String value) {
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
    public static StatusEnum fromValue(String input) {
      for (StatusEnum b : StatusEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("Status")
  private StatusEnum status = null;

  @JsonProperty("StatusUpdateDateTime")
  private OffsetDateTime statusUpdateDateTime = null;

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

  @JsonProperty("CutOffDateTime")
  private OffsetDateTime cutOffDateTime = null;

  @JsonProperty("ExpectedExecutionDateTime")
  private OffsetDateTime expectedExecutionDateTime = null;

  @JsonProperty("ExpectedSettlementDateTime")
  private OffsetDateTime expectedSettlementDateTime = null;

  @JsonProperty("Charges")
  private List<OBWriteDomesticConsentResponse5DataCharges> charges = null;

  @JsonProperty("Initiation")
  private OBWriteDomesticScheduled2DataInitiation initiation = null;

  @JsonProperty("Authorisation")
  private OBWriteDomesticConsent4DataAuthorisation authorisation = null;

  @JsonProperty("SCASupportData")
  private OBWriteDomesticConsent4DataSCASupportData scASupportData = null;

  @JsonProperty("Debtor")
  private OBDebtorIdentification1 debtor = null;

  public OBWriteDomesticScheduledConsentResponse5Data consentId(String consentId) {
    this.consentId = consentId;
    return this;
  }

   /**
   * OB: Unique identification as assigned by the ASPSP to uniquely identify the consent resource.
   * @return consentId
  **/
  
  public String getConsentId() {
    return consentId;
  }

  public void setConsentId(String consentId) {
    this.consentId = consentId;
  }

  public OBWriteDomesticScheduledConsentResponse5Data creationDateTime(OffsetDateTime creationDateTime) {
    this.creationDateTime = creationDateTime;
    return this;
  }

   /**
   * Date and time at which the resource was created.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00
   * @return creationDateTime
  **/
  
  public OffsetDateTime getCreationDateTime() {
    return creationDateTime;
  }

  public void setCreationDateTime(OffsetDateTime creationDateTime) {
    this.creationDateTime = creationDateTime;
  }

  public OBWriteDomesticScheduledConsentResponse5Data status(StatusEnum status) {
    this.status = status;
    return this;
  }

   /**
   * Specifies the status of consent resource in code form.
   * @return status
  **/
  
  public StatusEnum getStatus() {
    return status;
  }

  public void setStatus(StatusEnum status) {
    this.status = status;
  }

  public OBWriteDomesticScheduledConsentResponse5Data statusUpdateDateTime(OffsetDateTime statusUpdateDateTime) {
    this.statusUpdateDateTime = statusUpdateDateTime;
    return this;
  }

   /**
   * Date and time at which the consent resource status was updated.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00
   * @return statusUpdateDateTime
  **/
  
  public OffsetDateTime getStatusUpdateDateTime() {
    return statusUpdateDateTime;
  }

  public void setStatusUpdateDateTime(OffsetDateTime statusUpdateDateTime) {
    this.statusUpdateDateTime = statusUpdateDateTime;
  }

  public OBWriteDomesticScheduledConsentResponse5Data permission(PermissionEnum permission) {
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

  public OBWriteDomesticScheduledConsentResponse5Data readRefundAccount(ReadRefundAccountEnum readRefundAccount) {
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

  public OBWriteDomesticScheduledConsentResponse5Data cutOffDateTime(OffsetDateTime cutOffDateTime) {
    this.cutOffDateTime = cutOffDateTime;
    return this;
  }

   /**
   * Specified cut-off date and time for the payment consent.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00
   * @return cutOffDateTime
  **/
  
  public OffsetDateTime getCutOffDateTime() {
    return cutOffDateTime;
  }

  public void setCutOffDateTime(OffsetDateTime cutOffDateTime) {
    this.cutOffDateTime = cutOffDateTime;
  }

  public OBWriteDomesticScheduledConsentResponse5Data expectedExecutionDateTime(OffsetDateTime expectedExecutionDateTime) {
    this.expectedExecutionDateTime = expectedExecutionDateTime;
    return this;
  }

   /**
   * Expected execution date and time for the payment resource.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00
   * @return expectedExecutionDateTime
  **/
  
  public OffsetDateTime getExpectedExecutionDateTime() {
    return expectedExecutionDateTime;
  }

  public void setExpectedExecutionDateTime(OffsetDateTime expectedExecutionDateTime) {
    this.expectedExecutionDateTime = expectedExecutionDateTime;
  }

  public OBWriteDomesticScheduledConsentResponse5Data expectedSettlementDateTime(OffsetDateTime expectedSettlementDateTime) {
    this.expectedSettlementDateTime = expectedSettlementDateTime;
    return this;
  }

   /**
   * Expected settlement date and time for the payment resource.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00
   * @return expectedSettlementDateTime
  **/
  
  public OffsetDateTime getExpectedSettlementDateTime() {
    return expectedSettlementDateTime;
  }

  public void setExpectedSettlementDateTime(OffsetDateTime expectedSettlementDateTime) {
    this.expectedSettlementDateTime = expectedSettlementDateTime;
  }

  public OBWriteDomesticScheduledConsentResponse5Data charges(List<OBWriteDomesticConsentResponse5DataCharges> charges) {
    this.charges = charges;
    return this;
  }

  public OBWriteDomesticScheduledConsentResponse5Data addChargesItem(OBWriteDomesticConsentResponse5DataCharges chargesItem) {
    if (this.charges == null) {
      this.charges = new ArrayList<>();
    }
    this.charges.add(chargesItem);
    return this;
  }

   /**
   * Get charges
   * @return charges
  **/
  
  public List<OBWriteDomesticConsentResponse5DataCharges> getCharges() {
    return charges;
  }

  public void setCharges(List<OBWriteDomesticConsentResponse5DataCharges> charges) {
    this.charges = charges;
  }

  public OBWriteDomesticScheduledConsentResponse5Data initiation(OBWriteDomesticScheduled2DataInitiation initiation) {
    this.initiation = initiation;
    return this;
  }

   /**
   * Get initiation
   * @return initiation
  **/
  
  public OBWriteDomesticScheduled2DataInitiation getInitiation() {
    return initiation;
  }

  public void setInitiation(OBWriteDomesticScheduled2DataInitiation initiation) {
    this.initiation = initiation;
  }

  public OBWriteDomesticScheduledConsentResponse5Data authorisation(OBWriteDomesticConsent4DataAuthorisation authorisation) {
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

  public OBWriteDomesticScheduledConsentResponse5Data scASupportData(OBWriteDomesticConsent4DataSCASupportData scASupportData) {
    this.scASupportData = scASupportData;
    return this;
  }

   /**
   * Get scASupportData
   * @return scASupportData
  **/
  
  public OBWriteDomesticConsent4DataSCASupportData getScASupportData() {
    return scASupportData;
  }

  public void setScASupportData(OBWriteDomesticConsent4DataSCASupportData scASupportData) {
    this.scASupportData = scASupportData;
  }

  public OBWriteDomesticScheduledConsentResponse5Data debtor(OBDebtorIdentification1 debtor) {
    this.debtor = debtor;
    return this;
  }

   /**
   * Get debtor
   * @return debtor
  **/
  
  public OBDebtorIdentification1 getDebtor() {
    return debtor;
  }

  public void setDebtor(OBDebtorIdentification1 debtor) {
    this.debtor = debtor;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OBWriteDomesticScheduledConsentResponse5Data obWriteDomesticScheduledConsentResponse5Data = (OBWriteDomesticScheduledConsentResponse5Data) o;
    return Objects.equals(this.consentId, obWriteDomesticScheduledConsentResponse5Data.consentId) &&
        Objects.equals(this.creationDateTime, obWriteDomesticScheduledConsentResponse5Data.creationDateTime) &&
        Objects.equals(this.status, obWriteDomesticScheduledConsentResponse5Data.status) &&
        Objects.equals(this.statusUpdateDateTime, obWriteDomesticScheduledConsentResponse5Data.statusUpdateDateTime) &&
        Objects.equals(this.permission, obWriteDomesticScheduledConsentResponse5Data.permission) &&
        Objects.equals(this.readRefundAccount, obWriteDomesticScheduledConsentResponse5Data.readRefundAccount) &&
        Objects.equals(this.cutOffDateTime, obWriteDomesticScheduledConsentResponse5Data.cutOffDateTime) &&
        Objects.equals(this.expectedExecutionDateTime, obWriteDomesticScheduledConsentResponse5Data.expectedExecutionDateTime) &&
        Objects.equals(this.expectedSettlementDateTime, obWriteDomesticScheduledConsentResponse5Data.expectedSettlementDateTime) &&
        Objects.equals(this.charges, obWriteDomesticScheduledConsentResponse5Data.charges) &&
        Objects.equals(this.initiation, obWriteDomesticScheduledConsentResponse5Data.initiation) &&
        Objects.equals(this.authorisation, obWriteDomesticScheduledConsentResponse5Data.authorisation) &&
        Objects.equals(this.scASupportData, obWriteDomesticScheduledConsentResponse5Data.scASupportData) &&
        Objects.equals(this.debtor, obWriteDomesticScheduledConsentResponse5Data.debtor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(consentId, creationDateTime, status, statusUpdateDateTime, permission, readRefundAccount, cutOffDateTime, expectedExecutionDateTime, expectedSettlementDateTime, charges, initiation, authorisation, scASupportData, debtor);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBWriteDomesticScheduledConsentResponse5Data {\n");
    
    sb.append("    consentId: ").append(toIndentedString(consentId)).append("\n");
    sb.append("    creationDateTime: ").append(toIndentedString(creationDateTime)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    statusUpdateDateTime: ").append(toIndentedString(statusUpdateDateTime)).append("\n");
    sb.append("    permission: ").append(toIndentedString(permission)).append("\n");
    sb.append("    readRefundAccount: ").append(toIndentedString(readRefundAccount)).append("\n");
    sb.append("    cutOffDateTime: ").append(toIndentedString(cutOffDateTime)).append("\n");
    sb.append("    expectedExecutionDateTime: ").append(toIndentedString(expectedExecutionDateTime)).append("\n");
    sb.append("    expectedSettlementDateTime: ").append(toIndentedString(expectedSettlementDateTime)).append("\n");
    sb.append("    charges: ").append(toIndentedString(charges)).append("\n");
    sb.append("    initiation: ").append(toIndentedString(initiation)).append("\n");
    sb.append("    authorisation: ").append(toIndentedString(authorisation)).append("\n");
    sb.append("    scASupportData: ").append(toIndentedString(scASupportData)).append("\n");
    sb.append("    debtor: ").append(toIndentedString(debtor)).append("\n");
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
