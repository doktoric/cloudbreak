package com.sequenceiq.redbeams.converter.database;


import static com.sequenceiq.cloudbreak.common.type.APIResourceType.RDS_CONFIG;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.sequenceiq.cloudbreak.api.endpoint.v4.common.DatabaseVendor;
import com.sequenceiq.cloudbreak.api.endpoint.v4.common.ResourceStatus;
import com.sequenceiq.cloudbreak.common.converter.MissingResourceNameGenerator;
import com.sequenceiq.cloudbreak.common.service.Clock;
import com.sequenceiq.cloudbreak.converter.AbstractConversionServiceAwareConverter;
import com.sequenceiq.redbeams.api.endpoint.v4.database.request.DatabaseV4Request;
import com.sequenceiq.redbeams.domain.DatabaseConfig;
import com.sequenceiq.redbeams.util.DatabaseVendorUtil;

@Component
public class DatabaseV4RequestToDatabaseConfigConverter  extends AbstractConversionServiceAwareConverter<DatabaseV4Request, DatabaseConfig> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseV4RequestToDatabaseConfigConverter.class);

    @Inject
    private MissingResourceNameGenerator missingResourceNameGenerator;

    @Inject
    private Clock clock;

    @Override
    public DatabaseConfig convert(DatabaseV4Request source) {
        DatabaseConfig databaseConfig = new DatabaseConfig();
        if (Strings.isNullOrEmpty(source.getName())) {
            databaseConfig.setName(missingResourceNameGenerator.generateName(RDS_CONFIG));
        } else {
            databaseConfig.setName(source.getName());
        }
        databaseConfig.setDescription(source.getDescription());
        databaseConfig.setConnectionURL(source.getConnectionURL());

        DatabaseVendor databaseVendor = DatabaseVendorUtil.getVendorByJdbcUrl(source).get();
        databaseConfig.setDatabaseVendor(databaseVendor);
        databaseConfig.setConnectionDriver(databaseVendor.connectionDriver());
        databaseConfig.setConnectionUserName(source.getConnectionUserName());
        databaseConfig.setConnectionPassword(source.getConnectionPassword());
        databaseConfig.setCreationDate(clock.getCurrentTimeMillis());
        databaseConfig.setStatus(ResourceStatus.USER_MANAGED);
        databaseConfig.setType(source.getType());
        databaseConfig.setEnvironmentId(source.getEnvironmentId());

        if (databaseConfig.getDatabaseVendor() != DatabaseVendor.POSTGRES && StringUtils.isEmpty(source.getConnectorJarUrl())) {
            String msg = String.format("The 'connectorJarUrl' field needs to be specified for database engine: '%s'.", databaseConfig.getDatabaseVendor());
            LOGGER.info(msg);
        }
        databaseConfig.setConnectorJarUrl(source.getConnectorJarUrl());
        return databaseConfig;
    }
}
