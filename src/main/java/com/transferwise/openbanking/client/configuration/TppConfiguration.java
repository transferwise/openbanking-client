package com.transferwise.openbanking.client.configuration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TppConfiguration {

    /**
     * The TPP organisation ID, within the Open Banking directory.
     */
    private String organisationId;

    /**
     * The ID of the software statement, within the Open Banking directory, used for the integrations with the ASPSPs.
     */
    private String softwareStatementId;

    /**
     * The ID of the key, within the Open Banking directory, used for signing data to send to ASPSPs.
     */
    private String signingKeyId;

    /**
     * The URL that ASPSPs will redirect the user back to once the authorisation process is finished.
     */
    private String redirectUrl;
}
