/*
 * OBIE VRP Profile
 * VRP OpenAPI Specification
 *
 * OpenAPI spec version: 3.1.10
 * Contact: ServiceDesk@openbanking.org.uk
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package com.transferwise.openbanking.client.api.payment.v3.model.vrp;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

/**
 * OBDomesticVRPRequestData
 */

public class OBDomesticVRPRequestData implements Serializable {
  private static final long serialVersionUID = 1L;

  @JsonProperty("ConsentId")
  private String consentId;

  @JsonProperty("PSUAuthenticationMethod")
  private OBVRPAuthenticationMethods psUAuthenticationMethod = null;

  @JsonProperty("PSUInteractionType")
  private OBVRPInteractionTypes psUInteractionType = null;

  @JsonProperty("Initiation")
  private OBDomesticVRPInitiation initiation = null;

  @JsonProperty("Instruction")
  private OBDomesticVRPInstruction instruction = null;

  public OBDomesticVRPRequestData consentId(String consentId) {
    this.consentId = consentId;
    return this;
  }

   /**
   * Identifier for the Domestic VRP Consent that this payment is made under.
   * @return consentId
  **/
  
  public String getConsentId() {
    return consentId;
  }

  public void setConsentId(String consentId) {
    this.consentId = consentId;
  }

  public OBDomesticVRPRequestData psUAuthenticationMethod(OBVRPAuthenticationMethods psUAuthenticationMethod) {
    this.psUAuthenticationMethod = psUAuthenticationMethod;
    return this;
  }

   /**
   * Get psUAuthenticationMethod
   * @return psUAuthenticationMethod
  **/
  
  public OBVRPAuthenticationMethods getPsUAuthenticationMethod() {
    return psUAuthenticationMethod;
  }

  public void setPsUAuthenticationMethod(OBVRPAuthenticationMethods psUAuthenticationMethod) {
    this.psUAuthenticationMethod = psUAuthenticationMethod;
  }

  public OBDomesticVRPRequestData psUInteractionType(OBVRPInteractionTypes psUInteractionType) {
    this.psUInteractionType = psUInteractionType;
    return this;
  }

   /**
   * Get psUInteractionType
   * @return psUInteractionType
  **/
  
  public OBVRPInteractionTypes getPsUInteractionType() {
    return psUInteractionType;
  }

  public void setPsUInteractionType(OBVRPInteractionTypes psUInteractionType) {
    this.psUInteractionType = psUInteractionType;
  }

  public OBDomesticVRPRequestData initiation(OBDomesticVRPInitiation initiation) {
    this.initiation = initiation;
    return this;
  }

   /**
   * Get initiation
   * @return initiation
  **/
  
  public OBDomesticVRPInitiation getInitiation() {
    return initiation;
  }

  public void setInitiation(OBDomesticVRPInitiation initiation) {
    this.initiation = initiation;
  }

  public OBDomesticVRPRequestData instruction(OBDomesticVRPInstruction instruction) {
    this.instruction = instruction;
    return this;
  }

   /**
   * Get instruction
   * @return instruction
  **/
  
  public OBDomesticVRPInstruction getInstruction() {
    return instruction;
  }

  public void setInstruction(OBDomesticVRPInstruction instruction) {
    this.instruction = instruction;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OBDomesticVRPRequestData obDomesticVRPRequestData = (OBDomesticVRPRequestData) o;
    return Objects.equals(this.consentId, obDomesticVRPRequestData.consentId) &&
        Objects.equals(this.psUAuthenticationMethod, obDomesticVRPRequestData.psUAuthenticationMethod) &&
        Objects.equals(this.psUInteractionType, obDomesticVRPRequestData.psUInteractionType) &&
        Objects.equals(this.initiation, obDomesticVRPRequestData.initiation) &&
        Objects.equals(this.instruction, obDomesticVRPRequestData.instruction);
  }

  @Override
  public int hashCode() {
    return Objects.hash(consentId, psUAuthenticationMethod, psUInteractionType, initiation, instruction);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OBDomesticVRPRequestData {\n");
    
    sb.append("    consentId: ").append(toIndentedString(consentId)).append("\n");
    sb.append("    psUAuthenticationMethod: ").append(toIndentedString(psUAuthenticationMethod)).append("\n");
    sb.append("    psUInteractionType: ").append(toIndentedString(psUInteractionType)).append("\n");
    sb.append("    initiation: ").append(toIndentedString(initiation)).append("\n");
    sb.append("    instruction: ").append(toIndentedString(instruction)).append("\n");
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

