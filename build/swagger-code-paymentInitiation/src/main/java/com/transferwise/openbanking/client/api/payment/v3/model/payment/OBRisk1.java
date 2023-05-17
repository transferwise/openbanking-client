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
 * The Risk section is sent by the initiating party to the ASPSP. It is used to specify additional details for risk scoring for Payments.
 */


public class OBRisk1 implements Serializable{
  private static final long serialVersionUID = 1L;
  /**
   * Specifies the payment context * BillPayment - @deprecated * EcommerceGoods - @deprecated * EcommerceServices - @deprecated * Other - @deprecated * PartyToParty - @deprecated 
   */
  public enum PaymentContextCodeEnum {
    BILLINGGOODSANDSERVICESINADVANCE("BillingGoodsAndServicesInAdvance"),
    BILLINGGOODSANDSERVICESINARREARS("BillingGoodsAndServicesInArrears"),
    PISPPAYEE("PispPayee"),
    ECOMMERCEMERCHANTINITIATEDPAYMENT("EcommerceMerchantInitiatedPayment"),
    FACETOFACEPOINTOFSALE("FaceToFacePointOfSale"),
    TRANSFERTOSELF("TransferToSelf"),
    TRANSFERTOTHIRDPARTY("TransferToThirdParty"),
    BILLPAYMENT("BillPayment"),
    ECOMMERCEGOODS("EcommerceGoods"),
    ECOMMERCESERVICES("EcommerceServices"),
    OTHER("Other"),
    PARTYTOPARTY("PartyToParty");

    private String value;

    PaymentContextCodeEnum(String value) {
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
    public static PaymentContextCodeEnum fromValue(String input) {
      for (PaymentContextCodeEnum b : PaymentContextCodeEnum.values()) {
        if (b.value.equals(input)) {
          return b;
        }
      }
      return null;
    }

  }  @JsonProperty("PaymentContextCode")
  private PaymentContextCodeEnum paymentContextCode = null;

  @JsonProperty("MerchantCategoryCode")
  private String merchantCategoryCode = null;

  @JsonProperty("MerchantCustomerIdentification")
  private String merchantCustomerIdentification = null;

  @JsonProperty("ContractPresentInidicator")
  private Boolean contractPresentInidicator = null;

  @JsonProperty("BeneficiaryPrepopulatedIndicator")
  private Boolean beneficiaryPrepopulatedIndicator = null;

  @JsonProperty("PaymentPurposeCode")
  private String paymentPurposeCode = null;

  @JsonProperty("BeneficiaryAccountType")
  private OBExternalExtendedAccountType1Code beneficiaryAccountType = null;

  @JsonProperty("DeliveryAddress")
  private OBRisk1DeliveryAddress deliveryAddress = null;

  public OBRisk1 paymentContextCode(PaymentContextCodeEnum paymentContextCode) {
    this.paymentContextCode = paymentContextCode;
    return this;
  }

   /**
   * Specifies the payment context * BillPayment - @deprecated * EcommerceGoods - @deprecated * EcommerceServices - @deprecated * Other - @deprecated * PartyToParty - @deprecated 
   * @return paymentContextCode
  **/
  
  public PaymentContextCodeEnum getPaymentContextCode() {
    return paymentContextCode;
  }

  public void setPaymentContextCode(PaymentContextCodeEnum paymentContextCode) {
    this.paymentContextCode = paymentContextCode;
  }

  public OBRisk1 merchantCategoryCode(String merchantCategoryCode) {
    this.merchantCategoryCode = merchantCategoryCode;
    return this;
  }

   /**
   * Category code conform to ISO 18245, related to the type of services or goods the merchant provides for the transaction.
   * @return merchantCategoryCode
  **/
  
  public String getMerchantCategoryCode() {
    return merchantCategoryCode;
  }

  public void setMerchantCategoryCode(String merchantCategoryCode) {
    this.merchantCategoryCode = merchantCategoryCode;
  }

  public OBRisk1 merchantCustomerIdentification(String merchantCustomerIdentification) {
    this.merchantCustomerIdentification = merchantCustomerIdentification;
    return this;
  }

   /**
   * The unique customer identifier of the PSU with the merchant.
   * @return merchantCustomerIdentification
  **/
  
  public String getMerchantCustomerIdentification() {
    return merchantCustomerIdentification;
  }

  public void setMerchantCustomerIdentification(String merchantCustomerIdentification) {
    this.merchantCustomerIdentification = merchantCustomerIdentification;
  }

  public OBRisk1 contractPresentInidicator(Boolean contractPresentInidicator) {
    this.contractPresentInidicator = contractPresentInidicator;
    return this;
  }

   /**
   * Indicates if Payee has a contractual relationship with the PISP.
   * @return contractPresentInidicator
  **/
  
  public Boolean isContractPresentInidicator() {
    return contractPresentInidicator;
  }

  public void setContractPresentInidicator(Boolean contractPresentInidicator) {
    this.contractPresentInidicator = contractPresentInidicator;
  }

  public OBRisk1 beneficiaryPrepopulatedIndicator(Boolean beneficiaryPrepopulatedIndicator) {
    this.beneficiaryPrepopulatedIndicator = beneficiaryPrepopulatedIndicator;
    return this;
  }

   /**
   * Indicates if PISP has immutably prepopulated payment details in for the PSU.
   * @return beneficiaryPrepopulatedIndicator
  **/
  
  public Boolean isBeneficiaryPrepopulatedIndicator() {
    return beneficiaryPrepopulatedIndicator;
  }

  public void setBeneficiaryPrepopulatedIndicator(Boolean beneficiaryPrepopulatedIndicator) {
    this.beneficiaryPrepopulatedIndicator = beneficiaryPrepopulatedIndicator;
  }

  public OBRisk1 paymentPurposeCode(String paymentPurposeCode) {
    this.paymentPurposeCode = paymentPurposeCode;
    return this;
  }

   /**
   * Category code, related to the type of services or goods that corresponds to the underlying purpose of the payment that conforms to Recommended UK Purpose Code in ISO 20022 Payment Messaging List
   * @return paymentPurposeCode
  **/
  
  public String getPaymentPurposeCode() {
    return paymentPurposeCode;
  }

  public void setPaymentPurposeCode(String paymentPurposeCode) {
    this.paymentPurposeCode = paymentPurposeCode;
  }

  public OBRisk1 beneficiaryAccountType(OBExternalExtendedAccountType1Code beneficiaryAccountType) {
    this.beneficiaryAccountType = beneficiaryAccountType;
    return this;
  }

   /**
   * Get beneficiaryAccountType
   * @return beneficiaryAccountType
  **/
  
  public OBExternalExtendedAccountType1Code getBeneficiaryAccountType() {
    return beneficiaryAccountType;
  }

  public void setBeneficiaryAccountType(OBExternalExtendedAccountType1Code beneficiaryAccountType) {
    this.beneficiaryAccountType = beneficiaryAccountType;
  }

  public OBRisk1 deliveryAddress(OBRisk1DeliveryAddress deliveryAddress) {
    this.deliveryAddress = deliveryAddress;
    return this;
  }

   /**
   * Get deliveryAddress
   * @return deliveryAddress
  **/
  
  public OBRisk1DeliveryAddress getDeliveryAddress() {
    return deliveryAddress;
  }

  public void setDeliveryAddress(OBRisk1DeliveryAddress deliveryAddress) {
    this.deliveryAddress = deliveryAddress;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OBRisk1 obRisk1 = (OBRisk1) o;
    return Objects.equals(this.paymentContextCode, obRisk1.paymentContextCode) &&
        Objects.equals(this.merchantCategoryCode, obRisk1.merchantCategoryCode) &&
        Objects.equals(this.merchantCustomerIdentification, obRisk1.merchantCustomerIdentification) &&
        Objects.equals(this.contractPresentInidicator, obRisk1.contractPresentInidicator) &&
        Objects.equals(this.beneficiaryPrepopulatedIndicator, obRisk1.beneficiaryPrepopulatedIndicator) &&
        Objects.equals(this.paymentPurposeCode, obRisk1.paymentPurposeCode) &&
        Objects.equals(this.beneficiaryAccountType, obRisk1.beneficiaryAccountType) &&
        Objects.equals(this.deliveryAddress, obRisk1.deliveryAddress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(paymentContextCode, merchantCategoryCode, merchantCustomerIdentification, contractPresentInidicator, beneficiaryPrepopulatedIndicator, paymentPurposeCode, beneficiaryAccountType, deliveryAddress);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBRisk1 {\n");
    
    sb.append("    paymentContextCode: ").append(toIndentedString(paymentContextCode)).append("\n");
    sb.append("    merchantCategoryCode: ").append(toIndentedString(merchantCategoryCode)).append("\n");
    sb.append("    merchantCustomerIdentification: ").append(toIndentedString(merchantCustomerIdentification)).append("\n");
    sb.append("    contractPresentInidicator: ").append(toIndentedString(contractPresentInidicator)).append("\n");
    sb.append("    beneficiaryPrepopulatedIndicator: ").append(toIndentedString(beneficiaryPrepopulatedIndicator)).append("\n");
    sb.append("    paymentPurposeCode: ").append(toIndentedString(paymentPurposeCode)).append("\n");
    sb.append("    beneficiaryAccountType: ").append(toIndentedString(beneficiaryAccountType)).append("\n");
    sb.append("    deliveryAddress: ").append(toIndentedString(deliveryAddress)).append("\n");
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