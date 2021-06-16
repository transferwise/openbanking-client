package com.transferwise.openbanking.client.configuration;

import com.transferwise.openbanking.client.oauth.domain.Scope;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareStatementDetails {

    /**
     * The TPP organisation ID, within the Open Banking directory.
     */
    private String organisationId;

    /**
     * The ID of the software statement, within the Open Banking directory, used for the integrations with the ASPSPs.
     */
    private String softwareStatementId;

    /**
     * The URLs that ASPSPs can redirect the user back to once the authorisation process is finished.
     */
    private List<String> redirectUrls;

    /**
     * The permissions that the TPP has, according to the National Competent Authority (the FCA), and wants to use when
     * registering with an ASPSP.
     */
    private List<Scope> permissions;
}
