package com.transferwise.openbanking.client.api.event;

import com.transferwise.openbanking.client.api.payment.v3.model.event.OBEventSubscription1;
import com.transferwise.openbanking.client.api.payment.v3.model.event.OBEventSubscriptionResponse1;
import com.transferwise.openbanking.client.api.payment.v3.model.event.OBEventSubscriptionsResponse1;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;

/**
 * Interface specifying operations supported for open banking event API in 3.1.10.
 *
 * @since 12.0.0
 */
@SuppressWarnings("checkstyle:abbreviationaswordinname")
public interface EventClient {

    /**
     * Subscribe to an event.
     *
     * @param eventSubscriptionRequest  The request for event subscription.
     * @param aspspDetails  The details of the ASPSP.
     * @param softwareStatementDetails  The details of the software statement for ASPSP
     *                                 registration.
     * @return OBEventSubscriptionResponse1 The details of event subscribed.
     * @throws EventApiCallException    If subscribe API call to ASPSP fails
     */
    OBEventSubscriptionResponse1 createEventSubscription(
        OBEventSubscription1 eventSubscriptionRequest,
        AspspDetails aspspDetails,
        SoftwareStatementDetails softwareStatementDetails);

    /**
     * Get all events.
     *
     * @param aspspDetails  The details of the ASPSP.
     * @return OBEventSubscriptionsResponse1    List of subscribed events.
     * @throws EventApiCallException    If get all events API call to ASPSP fails
     */
    public OBEventSubscriptionsResponse1 getEventSubscriptions(AspspDetails aspspDetails);

    /**
     * Change a subscribed event.
     *
     * @param changedResponse   The details for changing a subscribed event.
     * @param aspspDetails  The details of the ASPSP.
     * @return OBEventSubscriptionResponse1 The details of the changed event resource.
     * @throws EventApiCallException    If change Event API call to ASPSP fails
     */
    public OBEventSubscriptionResponse1 changeEventSubscription(
        OBEventSubscriptionResponse1 changedResponse,
        AspspDetails aspspDetails,
        SoftwareStatementDetails softwareStatementDetails);

    /**
     * Delete an event.
     *
     * @param eventSubscriptionId   The Unique identification for an event resource.
     * @param aspspDetails  The details of the ASPSP.
     * @throws EventApiCallException    If delete event API call to ASPSP fails
     */
    public void deleteEventSubscription(String eventSubscriptionId, AspspDetails aspspDetails);
}
