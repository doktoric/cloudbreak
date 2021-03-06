package com.sequenceiq.redbeams.converter.v4.databaseserver;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.sequenceiq.redbeams.api.endpoint.v4.databaseserver.requests.DatabaseServerV4Request;
import com.sequenceiq.redbeams.domain.DatabaseServerConfig;

public class DatabaseServerV4RequestToDatabaseServerConfigConverterTest {

    private DatabaseServerV4RequestToDatabaseServerConfigConverter converter;

    @Before
    public void setUp() {
        converter = new DatabaseServerV4RequestToDatabaseServerConfigConverter();
    }

    @Test
    public void testConversion() {
        DatabaseServerV4Request request = new DatabaseServerV4Request();
        request.setName("myserver");
        request.setDescription("mine not yours");
        request.setHost("myserver.db.example.com");
        request.setPort(5432);
        request.setDatabaseVendor("postgres");
        request.setConnectionUserName("root");
        request.setConnectionPassword("cloudera");
        request.setConnectorJarUrl("http://drivers.example.com/postgresql.jar");
        request.setEnvironmentId("myenvironment");

        DatabaseServerConfig server = converter.convert(request);

        assertEquals(request.getName(), server.getName());
        assertEquals(request.getDescription(), server.getDescription());
        assertEquals(request.getHost(), server.getHost());
        assertEquals(request.getPort(), server.getPort());
        assertEquals(request.getDatabaseVendor(), server.getDatabaseVendor().databaseType());
        assertEquals(request.getConnectionUserName(), server.getConnectionUserName());
        assertEquals(request.getConnectionPassword(), server.getConnectionPassword());
        assertEquals(request.getConnectorJarUrl(), server.getConnectorJarUrl());
        assertEquals(request.getEnvironmentId(), server.getEnvironmentId());
    }

}
