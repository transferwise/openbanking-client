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
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.io.Serializable;
/**
 * Further detailed information on the exchange rate that has been used in the payment transaction.
 */


public class OBWriteInternationalConsentResponse6DataExchangeRateInformation implements Serializable{
  private static final long serialVersionUID = 1L;
  @JsonProperty("UnitCurrency")
  private String unitCurrency = null;

  @JsonProperty("ExchangeRate")
  private BigDecimal exchangeRate = null;

  /**
   * Specifies the type used to complete the currency exchange.
   */
  public enum RateTypeEnum {
    ACTUAL("Actual"),
    AGREED("Agreed"),
    INDICATIVE("Indicative");

    private String value;

    RateTypeEnum(String value) {
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
    public static RateTypeEnum fromValue(String input) {
      for (RateTypeEnum b : RateTypeEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("RateType")
  private RateTypeEnum rateType = null;

  @JsonProperty("ContractIdentification")
  private String contractIdentification = null;

  @JsonProperty("ExpirationDateTime")
  private OffsetDateTime expirationDateTime = null;

  public OBWriteInternationalConsentResponse6DataExchangeRateInformation unitCurrency(String unitCurrency) {
    this.unitCurrency = unitCurrency;
    return this;
  }

   /**
   * Currency in which the rate of exchange is expressed in a currency exchange. In the example 1GBP &#x3D; xxxCUR, the unit currency is GBP.
   * @return unitCurrency
  **/
  
  public String getUnitCurrency() {
    return unitCurrency;
  }

  public void setUnitCurrency(String unitCurrency) {
    this.unitCurrency = unitCurrency;
  }

  public OBWriteInternationalConsentResponse6DataExchangeRateInformation exchangeRate(BigDecimal exchangeRate) {
    this.exchangeRate = exchangeRate;
    return this;
  }

   /**
   * The factor used for conversion of an amount from one currency to another. This reflects the price at which one currency was bought with another currency.
   * @return exchangeRate
  **/
  
  public BigDecimal getExchangeRate() {
    return exchangeRate;
  }

  public void setExchangeRate(BigDecimal exchangeRate) {
    this.exchangeRate = exchangeRate;
  }

  public OBWriteInternationalConsentResponse6DataExchangeRateInformation rateType(RateTypeEnum rateType) {
    this.rateType = rateType;
    return this;
  }

   /**
   * Specifies the type used to complete the currency exchange.
   * @return rateType
  **/
  
  public RateTypeEnum getRateType() {
    return rateType;
  }

  public void setRateType(RateTypeEnum rateType) {
    this.rateType = rateType;
  }

  public OBWriteInternationalConsentResponse6DataExchangeRateInformation contractIdentification(String contractIdentification) {
    this.contractIdentification = contractIdentification;
    return this;
  }

   /**
   * Unique and unambiguous reference to the foreign exchange contract agreed between the initiating party/creditor and the debtor agent.
   * @return contractIdentification
  **/
  
  public String getContractIdentification() {
    return contractIdentification;
  }

  public void setContractIdentification(String contractIdentification) {
    this.contractIdentification = contractIdentification;
  }

  public OBWriteInternationalConsentResponse6DataExchangeRateInformation expirationDateTime(OffsetDateTime expirationDateTime) {
    this.expirationDateTime = expirationDateTime;
    return this;
  }

   /**
   * Specified date and time the exchange rate agreement will expire.All dates in the JSON payloads are represented in ISO 8601 date-time format.  All date-time fields in responses must include the timezone. An example is below: 2017-04-05T10:43:07+00:00
   * @return expirationDateTime
  **/
  
  public OffsetDateTime getExpirationDateTime() {
    return expirationDateTime;
  }

  public void setExpirationDateTime(OffsetDateTime expirationDateTime) {
    this.expirationDateTime = expirationDateTime;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OBWriteInternationalConsentResponse6DataExchangeRateInformation obWriteInternationalConsentResponse6DataExchangeRateInformation = (OBWriteInternationalConsentResponse6DataExchangeRateInformation) o;
    return Objects.equals(this.unitCurrency, obWriteInternationalConsentResponse6DataExchangeRateInformation.unitCurrency) &&
        Objects.equals(this.exchangeRate, obWriteInternationalConsentResponse6DataExchangeRateInformation.exchangeRate) &&
        Objects.equals(this.rateType, obWriteInternationalConsentResponse6DataExchangeRateInformation.rateType) &&
        Objects.equals(this.contractIdentification, obWriteInternationalConsentResponse6DataExchangeRateInformation.contractIdentification) &&
        Objects.equals(this.expirationDateTime, obWriteInternationalConsentResponse6DataExchangeRateInformation.expirationDateTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(unitCurrency, exchangeRate, rateType, contractIdentification, expirationDateTime);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBWriteInternationalConsentResponse6DataExchangeRateInformation {\n");
    
    sb.append("    unitCurrency: ").append(toIndentedString(unitCurrency)).append("\n");
    sb.append("    exchangeRate: ").append(toIndentedString(exchangeRate)).append("\n");
    sb.append("    rateType: ").append(toIndentedString(rateType)).append("\n");
    sb.append("    contractIdentification: ").append(toIndentedString(contractIdentification)).append("\n");
    sb.append("    expirationDateTime: ").append(toIndentedString(expirationDateTime)).append("\n");
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
