package com.transferwise.openbanking.client.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.transferwise.openbanking.client.api.payment.v3.model.vrp.OBDomesticVRPConsentResponseData.StatusEnum;
import java.io.IOException;

public class VrpConsentResponseStatusEnumDeserializer
    extends StdDeserializer<StatusEnum> {

    protected VrpConsentResponseStatusEnumDeserializer() {
        this(null);
    }

    protected VrpConsentResponseStatusEnumDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public StatusEnum deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);
        String status = node.asText();
        if ("Revoked".equals(status)) {
            return StatusEnum.REJECTED;
        }
        return StatusEnum.fromValue(status);
    }

}
