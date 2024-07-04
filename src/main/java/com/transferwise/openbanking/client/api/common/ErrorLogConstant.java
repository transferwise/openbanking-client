package com.transferwise.openbanking.client.api.common;

public class ErrorLogConstant {

    // RestEventClient
    public static final String ON_ERROR_GET_EVENT_RESOURCE_LOG = "Call to get event resource endpoint failed";
    public static final String ON_ERROR_CHANGE_EVENT_RESOURCE_LOG = "Call to change event resource endpoint failed";
    public static final String ON_ERROR_SUB_EVENT_LOG = "Call to subscribe event endpoint failed";
    public static final String ON_ERROR_DELETE_EVENT_LOG = "Call to delete event endpoint failed";

    // RestVrpClient
    public static final String ON_ERROR_GET_VRP_COF_LOG = "Call to get VRP confirmation of funds endpoint failed";
    public static final String ON_ERROR_GET_VRP_CONSENT_LOG = "Call to get VRP consent endpoint failed";
    public static final String ON_ERROR_DELETE_VRP_CONSENT_LOG = "Call to delete VRP consent endpoint";
    public static final String ON_ERROR_SUBMIT_VRP_LOG = "Call to submit VRP endpoint failed";
    public static final String ON_ERROR_GET_VRP_LOG = "Call to get VRP endpoint failed";
    public static final String ON_ERROR_GET_VRP_DETAILS_LOG = "Call to get VRP details endpoint failed";

    // RestPaymentClient
    public static final String ON_ERROR_CREATE_PAYMENT_LOG = "Call to create payment consent endpoint failed";
    public static final String ON_ERROR_SUBMIT_PAYMENT_LOG = "Call to submit payment endpoint failed";
    public static final String ON_ERROR_GET_PAYMENT_CONSENT_LOG = "Call to get payment consent endpoint failed";
    public static final String ON_ERROR_GET_PAYMENT_LOG = "Call to get payment endpoint failed";
    public static final String ON_ERROR_GET_COF_LOG = "Call to get confirmation of funds endpoint failed";

    // RestRegistrationClient
    public static final String ON_RECEIVE_REGISTER_LOG = "Received registration response";
    public static final String ON_RECEIVE_UPDATE_LOG = "Received update registration response";
    public static final String ON_RECEIVE_DELETE_LOG = "Received delete registration response";

    public static final String ON_ERROR_REGISTER_LOG = "Call to register client endpoint failed";
    public static final String ON_ERROR_UPDATE_LOG = "Call to update registration endpoint failed";
    public static final String ON_ERROR_DELETE_LOG = "Call to delete registration endpoint failed";

    // RestOAuthClient
    public static final String ON_ERROR_TOKEN_LOG = "Call to token endpoint failed";
}
