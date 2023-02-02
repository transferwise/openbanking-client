package com.transferwise.openbanking.client.api.event;

import com.transferwise.openbanking.client.api.payment.v3.model.event.OBEventSubscription1;
import com.transferwise.openbanking.client.api.payment.v3.model.event.OBEventSubscriptionsResponse1;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;
import com.transferwise.openbanking.client.api.payment.v3.model.event.OBEventSubscriptionResponse1;

/**
 *  Interface specifying operations supported for event API open banking in 3.1.10
 */
public interface EventClient {

    /**
     * Create an Event Subscription :
     * Asking a ASPSP to notify callback whenever there are updates on the given event.
     *
     * @param eventSubscriptionRequest  request for event contains {callback URL, event type, version}
     * @param aspspDetails -> detail of ASPSP which has the event to subscribe.
     * @param softwareStatementDetails ->  The details of the software statement that the ASPSP registration uses
     * @return OBEventSubscriptionResponse1 -> Event {subscription id, callback URL, version, event types}
     */
    OBEventSubscriptionResponse1 subscribeToAnEvent(
        OBEventSubscription1 eventSubscriptionRequest,
        AspspDetails aspspDetails,
        SoftwareStatementDetails softwareStatementDetails);

    /**
     * Get all events from the given ASPSP.
     *
     * @param aspspDetails details of ASPSP which has the subscribed event resources.
     * @return OBEventSubscriptionsResponse1 set of events subscribed to the given ASPSP.
     */
    public OBEventSubscriptionsResponse1 getEventResources( AspspDetails aspspDetails);

    /**
     * Ask ASPSP to delete the given subscribed Event resource.
     *
     * @param eventSubscriptionId Unique identification as assigned by the ASPSP,
     *                           to uniquely identify the callback URL resource.
     * @param aspspDetails details of ASPSP which has the subscribed event resources.
     */
    public void deleteAnEventResource(
        String eventSubscriptionId, AspspDetails aspspDetails);
}
