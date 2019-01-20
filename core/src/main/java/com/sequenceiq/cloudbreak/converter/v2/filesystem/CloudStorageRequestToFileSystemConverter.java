package com.sequenceiq.cloudbreak.converter.v2.filesystem;

import static com.sequenceiq.cloudbreak.common.type.APIResourceType.FILESYSTEM;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sequenceiq.cloudbreak.api.model.filesystem.AdlsFileSystem;
import com.sequenceiq.cloudbreak.api.model.filesystem.AdlsGen2FileSystem;
import com.sequenceiq.cloudbreak.api.model.filesystem.BaseFileSystem;
import com.sequenceiq.cloudbreak.api.model.filesystem.GcsFileSystem;
import com.sequenceiq.cloudbreak.api.model.filesystem.S3FileSystem;
import com.sequenceiq.cloudbreak.api.model.filesystem.WasbFileSystem;
import com.sequenceiq.cloudbreak.api.model.v2.CloudStorageRequest;
import com.sequenceiq.cloudbreak.api.model.v2.filesystem.CloudStorageParameters;
import com.sequenceiq.cloudbreak.controller.exception.BadRequestException;
import com.sequenceiq.cloudbreak.converter.AbstractConversionServiceAwareConverter;
import com.sequenceiq.cloudbreak.converter.util.FileSystemConvertUtil;
import com.sequenceiq.cloudbreak.domain.FileSystem;
import com.sequenceiq.cloudbreak.domain.StorageLocation;
import com.sequenceiq.cloudbreak.domain.StorageLocationCategory;
import com.sequenceiq.cloudbreak.domain.StorageLocations;
import com.sequenceiq.cloudbreak.domain.json.Json;
import com.sequenceiq.cloudbreak.service.MissingResourceNameGenerator;
import com.sequenceiq.cloudbreak.service.filesystem.FileSystemResolver;

@Component
public class CloudStorageRequestToFileSystemConverter extends AbstractConversionServiceAwareConverter<CloudStorageRequest, FileSystem> {

    @Inject
    private MissingResourceNameGenerator nameGenerator;

    @Inject
    private FileSystemResolver fileSystemResolver;

    @Inject
    private FileSystemConvertUtil fileSystemConvertUtil;

    @Override
    public FileSystem convert(CloudStorageRequest source) {
        FileSystem fileSystem = new FileSystem();
        fileSystem.setName(nameGenerator.generateName(FILESYSTEM));
        fileSystem.setDefaultFs(false);
        CloudStorageParameters cloudStorageParameters = fileSystemResolver.propagateConfiguration(source);
        fileSystem.setType(cloudStorageParameters.getType());

        Set<StorageLocation> locations = new HashSet<>();
        fileSystemConvertUtil.populateStorageLocationsFromMap(StorageLocationCategory.OP_LOGS, source.getOpLogs(), locations);
        fileSystemConvertUtil.populateStorageLocationsFromMap(StorageLocationCategory.NOTEBOOK, source.getNotebook(), locations);
        fileSystemConvertUtil.populateStorageLocationsFromMap(StorageLocationCategory.WAREHOUSE, source.getWarehouse(), locations);
        fileSystemConvertUtil.populateStorageLocationsFromMap(StorageLocationCategory.AUDIT, source.getAudit(), locations);
        try {
            StorageLocations storageLocations = new StorageLocations();
            storageLocations.setLocations(locations);
            fileSystem.setLocations(new Json(storageLocations));
        } catch (JsonProcessingException e) {
            throw new BadRequestException(String.format("Storage locations could not be parsed: %s", source), e);
        }
        BaseFileSystem baseFileSystem = null;
        if (source.getAdls() != null) {
            baseFileSystem = getConversionService().convert(source.getAdls(), AdlsFileSystem.class);
        } else if (source.getGcs() != null) {
            baseFileSystem = getConversionService().convert(source.getGcs(), GcsFileSystem.class);
        } else if (source.getS3() != null) {
            baseFileSystem = getConversionService().convert(source.getS3(), S3FileSystem.class);
        } else if (source.getWasb() != null) {
            baseFileSystem = getConversionService().convert(source.getWasb(), WasbFileSystem.class);
        } else if (source.getAdlsGen2() != null) {
            baseFileSystem = getConversionService().convert(source.getAdlsGen2(), AdlsGen2FileSystem.class);
        }
        try {
            fileSystem.setConfigurations(new Json(baseFileSystem));
        } catch (JsonProcessingException e) {
            throw new BadRequestException(String.format("Storage configuration could not be parsed: %s", source), e);
        }
        return fileSystem;
    }

}
