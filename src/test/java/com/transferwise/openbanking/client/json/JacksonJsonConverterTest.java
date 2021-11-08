package com.transferwise.openbanking.client.json;

import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBExternalAccountIdentification4Code;
import com.transferwise.openbanking.client.api.payment.v3.model.payment.OBWriteDomestic2DataInitiationDebtorAccount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class JacksonJsonConverterTest {

    private static JacksonJsonConverter jsonConverter;

    @BeforeAll
    static void initAll() {
        jsonConverter = new JacksonJsonConverter();
    }

    @Test
    void writeValueAsStringProducesCorrectJson() {
        OBWriteDomestic2DataInitiationDebtorAccount object = new OBWriteDomestic2DataInitiationDebtorAccount()
            .schemeName(OBExternalAccountIdentification4Code.SORTCODEACCOUNTNUMBER)
            .name("Wise");

        String json = jsonConverter.writeValueAsString(object);

        Assertions.assertEquals("{\"SchemeName\":\"UK.OBIE.SortCodeAccountNumber\",\"Name\":\"Wise\"}", json);
    }

    @Test
    void readValueProducesCorrectObject() {
        String json = "{\"Name\": \"Wise\",\"Currency\": \"GBP\"}";

        OBWriteDomestic2DataInitiationDebtorAccount object = jsonConverter.readValue(json,
            OBWriteDomestic2DataInitiationDebtorAccount.class);

        Assertions.assertEquals("Wise", object.getName());
        Assertions.assertNull(object.getSchemeName());
    }

    @Test
    void readValueThrowsJsonReadExceptionOnJacksonException() {
        String json = "ABC";

        JsonReadException thrown = Assertions.assertThrows(JsonReadException.class,
            () -> jsonConverter.readValue(json, OBWriteDomestic2DataInitiationDebtorAccount.class));

        Assertions.assertEquals("ABC", thrown.getJson());
    }
}
