package com.transferwise.openbanking.client.api.event;

import com.transferwise.openbanking.client.api.payment.v3.model.event.OBEventSubscription1;
import com.transferwise.openbanking.client.api.payment.v3.model.event.OBEventSubscriptionsResponse1;
import com.transferwise.openbanking.client.configuration.AspspDetails;
import com.transferwise.openbanking.client.configuration.SoftwareStatementDetails;
import com.transferwise.openbanking.client.api.payment.v3.model.event.OBEventSubscriptionResponse1;

/**
 *  Interface specifying operations supported for event API open banking in 3.1.10
 *  https://openbankinguk.github.io/read-write-api-site3/v3.1.10/profiles/event-notification-api-profile.html
 */
public interface EventClient {

    /**
     * @param eventSubscriptionRequest  request for event contains {callback URL, event type, version}
     * @param aspspDetails -> detail of ASPSP which has the event to subscribe.
     * @param softwareStatementDetails ->  The details of the software statement that the ASPSP registration uses
     * @return OBEventSubscriptionResponse1 -> Event {subscription id, callback URL, version, event types}
     */
    OBEventSubscriptionResponse1 subscribeToAnEvent(OBEventSubscription1 eventSubscriptionRequest, AspspDetails aspspDetails,
        SoftwareStatementDetails softwareStatementDetails);
    
    /**
     *
     * @param aspspDetails detail of ASPSP which has the event to subscribe.
     * @return
     */
    public OBEventSubscriptionsResponse1 getEventResources( AspspDetails aspspDetails);

}
