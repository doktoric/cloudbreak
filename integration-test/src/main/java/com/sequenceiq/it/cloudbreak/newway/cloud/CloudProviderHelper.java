package com.sequenceiq.it.cloudbreak.newway.cloud;

import static com.sequenceiq.it.cloudbreak.newway.cloud.HostGroupType.COMPUTE;
import static com.sequenceiq.it.cloudbreak.newway.cloud.HostGroupType.MASTER;
import static com.sequenceiq.it.cloudbreak.newway.cloud.HostGroupType.WORKER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sequenceiq.cloudbreak.api.model.AmbariStackDetailsJson;
import com.sequenceiq.cloudbreak.api.model.RecoveryMode;
import com.sequenceiq.cloudbreak.api.model.ldap.LdapConfigRequest;
import com.sequenceiq.cloudbreak.api.model.stack.StackAuthenticationRequest;
import com.sequenceiq.cloudbreak.api.model.v2.AmbariV2Request;
import com.sequenceiq.cloudbreak.api.model.v2.CloudStorageRequest;
import com.sequenceiq.cloudbreak.api.model.v2.InstanceGroupV2Request;
import com.sequenceiq.cloudbreak.api.model.v2.NetworkV2Request;
import com.sequenceiq.cloudbreak.api.model.v2.filesystem.CloudStorageParameters;
import com.sequenceiq.it.cloudbreak.filesystem.CloudStorageTypePathPrefix;
import com.sequenceiq.it.cloudbreak.newway.CredentialEntity;
import com.sequenceiq.it.cloudbreak.newway.LdapConfigRequestDataCollector;
import com.sequenceiq.it.cloudbreak.newway.Stack;
import com.sequenceiq.it.cloudbreak.newway.StackAction;
import com.sequenceiq.it.cloudbreak.newway.StackCreation;
import com.sequenceiq.it.cloudbreak.newway.StackEntity;
import com.sequenceiq.it.cloudbreak.newway.TestParameter;

public abstract class CloudProviderHelper extends CloudProvider {

    public static final String DEFAULT_AMBARI_USER = "integrationtest.ambari.defaultAmbariUser";

    public static final String DEFAULT_AMBARI_PASSWORD = "integrationtest.ambari.defaultAmbariPassword";

    public static final String DEFAULT_AMBARI_PORT = "integrationtest.ambari.defaultAmbariPort";

    public static final int BEGIN_INDEX = 4;

    public static final String INTEGRATIONTEST_PUBLIC_KEY_FILE = "integrationtest.publicKeyFile";

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudProviderHelper.class);

    private static final String DEFAULT_DATALAKE_BLUEPRINT = "Data Lake: Apache Ranger, Apache Hive Metastore";

    private static final String RECOVERY_MODE = "RecoveryMode";

    private static final String AUTO = "auto";

    private static final String MANUAL = "manual";

    private final TestParameter testParameter;

    private String clusterNamePostfix;

    protected CloudProviderHelper(TestParameter testParameter) {
        LOGGER.info("TestParemeters length: {}", testParameter.size());
        this.testParameter = testParameter;
    }

    public static CloudProvider[] providersFactory(String provider, TestParameter testParameter) {
        CloudProvider[] results;
        String[] providerArray = provider.split(",");
        results = new CloudProvider[providerArray.length];
        for (int i = 0; i < providerArray.length; i++) {
            LOGGER.info("Provider: \'{}\'", providerArray[i].toLowerCase().trim());
            results[i] = providerFactory(providerArray[i].toLowerCase().trim(), testParameter);
        }
        return results;
    }

    public static CloudProvider providerFactory(String provider, TestParameter testParameter) {
        LOGGER.info("Provider: \'{}\'", provider.toLowerCase().trim());
        CloudProvider cloudProvider;
        switch (provider.toLowerCase().trim()) {
            case AwsCloudProvider.AWS:
                cloudProvider = new AwsCloudProvider(testParameter);
                break;
            case AzureCloudProvider.AZURE:
                cloudProvider = new AzureCloudProvider(testParameter);
                break;
            case GcpCloudProvider.GCP:
                cloudProvider = new GcpCloudProvider(testParameter);
                break;
            case OpenstackCloudProvider.OPENSTACK:
                cloudProvider = new OpenstackCloudProvider(testParameter);
                break;
            case MockCloudProvider.MOCK:
                cloudProvider = new MockCloudProvider(testParameter);
                break;
            default:
                LOGGER.warn("could not determine cloud provider!");
                cloudProvider = null;

        }
        return cloudProvider;
    }

    @Override
    public CredentialEntity aValidCredential() {
        return aValidCredential(true);
    }

    public StackEntity aValidStackRequest() {
        return Stack.request()
                .withInputs(Collections.emptyMap())
                .withName(getClusterName())
                .withRegion(region())
                .withAvailabilityZone(availabilityZone())
                .withInstanceGroups(instanceGroups())
                .withNetwork(newNetwork())
                .withStackAuthentication(stackauth());
    }

    public StackEntity aValidAttachedStackRequest() {
        var request = new StackCreation(aValidStackRequest());
        request.setCreationStrategy(StackAction::determineNetworkFromDatalakeStack);
        return request.getStack();
    }

    @Override
    public Stack aValidStackCreated() {
        return (Stack) Stack.created()
                .withName(getClusterName())
                .withRegion(region())
                .withAvailabilityZone(availabilityZone())
                .withInstanceGroups(instanceGroups())
                .withNetwork(newNetwork())
                .withStackAuthentication(stackauth());
    }

    public Stack aValidAttachedClusterStackCreated(HostGroupType... groupTypes) {
        return (Stack) Stack.request()
                .withName(getClusterName())
                .withRegion(region())
                .withAvailabilityZone(availabilityZone())
                .withInstanceGroups(instanceGroups(groupTypes))
                .withNetwork(newNetwork())
                .withStackAuthentication(stackauth());
    }

    public abstract StackAuthenticationRequest stackauth();

    public abstract NetworkV2Request newNetwork();

    public abstract NetworkV2Request existingNetwork();

    public abstract NetworkV2Request existingSubnet();

    public List<InstanceGroupV2Request> instanceGroups() {
        return instanceGroups(MASTER, COMPUTE, WORKER);
    }

    public List<InstanceGroupV2Request> instanceGroups(HostGroupType... groupTypes) {
        List<InstanceGroupV2Request> requests = new ArrayList<>(groupTypes != null ? groupTypes.length : 0);
        if (groupTypes != null) {
            for (HostGroupType groupType : groupTypes) {
                requests.add(groupType.hostgroupRequest(this, testParameter));
            }
        }
        return requests;
    }

    public List<InstanceGroupV2Request> instanceGroups(String securityGroupId, HostGroupType... groupTypes) {
        List<InstanceGroupV2Request> requests = new ArrayList<>(groupTypes != null ? groupTypes.length : 0);
        if (groupTypes != null) {
            for (HostGroupType groupType : groupTypes) {
                requests.add(groupType.hostgroupRequest(this, testParameter, securityGroupId));
            }
        }
        return requests;
    }

    public List<InstanceGroupV2Request> instanceGroups(Set<String> recipes, HostGroupType... groupTypes) {
        List<InstanceGroupV2Request> requests = new ArrayList<>(groupTypes != null ? groupTypes.length : 0);
        if (groupTypes != null) {
            for (HostGroupType groupType : groupTypes) {
                requests.add(groupType.hostgroupRequest(this, testParameter, recipes));
            }
        }
        return requests;
    }

    @Override
    public RecoveryMode getRecoveryModeParam(String hostgroupName) {
        String argumentName = String.join("", hostgroupName, RECOVERY_MODE);
        String argumentValue = getTestParameter().getWithDefault(argumentName, MANUAL);
        if (argumentValue.equals(AUTO)) {
            return RecoveryMode.AUTO;
        } else {
            return RecoveryMode.MANUAL;
        }
    }

    public List<InstanceGroupV2Request> instanceGroups(String securityGroupId) {
        return instanceGroups(securityGroupId, MASTER, COMPUTE, WORKER);

    }

    public List<InstanceGroupV2Request> instanceGroups(Set<String> recipes) {
        return instanceGroups(recipes, MASTER, COMPUTE, WORKER);
    }

    @Override
    public AmbariV2Request ambariRequestWithBlueprintId(Long id) {
        AmbariV2Request req = new AmbariV2Request();
        req.setUserName(testParameter.get(DEFAULT_AMBARI_USER));
        req.setPassword(testParameter.get(DEFAULT_AMBARI_PASSWORD));
        req.setBlueprintId(id);
        req.setValidateRepositories(Boolean.TRUE);
        return req;
    }

    @Override
    public AmbariV2Request ambariRequestWithBlueprintNameAndCustomAmbari(String bluePrintName, String customAmbariVersion,
            String customAmbariRepoUrl, String customAmbariRepoGpgKey) {
        return ambariRequestWithBlueprintName(bluePrintName);
    }

    @Override
    public AmbariV2Request ambariRequestWithBlueprintName(String bluePrintName) {
        var req = new AmbariV2Request();
        req.setUserName(testParameter.get(DEFAULT_AMBARI_USER));
        req.setPassword(testParameter.get(DEFAULT_AMBARI_PASSWORD));
        req.setBlueprintName(bluePrintName);
        req.setValidateBlueprint(false);
        req.setValidateRepositories(Boolean.TRUE);
        req.setAmbariStackDetails(new AmbariStackDetailsJson());
        return req;
    }

    public LdapConfigRequest getLdap() {
        return LdapConfigRequestDataCollector.createLdapRequestWithProperties(testParameter);
    }

    public TestParameter getTestParameter() {
        return testParameter;
    }

    protected StorageLocationsInternal defaultDatalakeStorageLocations(CloudStorageTypePathPrefix type, String parameterToInsert) {
        StorageLocationsInternal locations = new StorageLocationsInternal();
        locations.setOpLogs(new LinkedHashMap<>(1));
        locations.setNotebook(new LinkedHashMap<>(1));
        var warehouse = new LinkedHashMap<String, String>(1);
        warehouse.put("hiveWarehouse", String.format("%s://%s/apps/hive/warehouse", type.getPrefix(), parameterToInsert));
        locations.setWarehouse(warehouse);
        var audit = new LinkedHashMap<String, String>(1);
        audit.put("rangerAudit", String.format("%s://%s/apps/ranger/audit", type.getPrefix(), parameterToInsert));
        locations.setAudit(audit);
        return locations;
    }

    protected CloudStorageRequest getCloudStorageForAttachedCluster(CloudStorageTypePathPrefix type, String parameterToInsert,
                                                                    CloudStorageParameters emptyType) {
        var request = new CloudStorageRequest();
        var opLogs = new LinkedHashMap<String, String>(1);
        opLogs.put("hiveWarehouse", String.format("%s://%s/attached/apps/hive/warehouse", type.getPrefix(), parameterToInsert));
        request.setOpLogs(opLogs);
        request.setNotebook(new LinkedHashMap<>(1));
        request.setWarehouse(new LinkedHashMap<>(1));
        request.setAudit(new LinkedHashMap<>(1));
        type.setParameterForRequest(request, emptyType);
        return request;
    }

    protected void setStorageLocations(CloudStorageRequest request, StorageLocationsInternal locations) {
        request.setOpLogs(locations.getOpLogs());
        request.setNotebook(locations.getNotebook());
        request.setWarehouse(locations.getWarehouse());
        request.setAudit(locations.getAudit());
    }

    protected String getDatalakeBlueprintName() {
        String blueprintName = testParameter.get("datalakeBlueprintName");
        return blueprintName != null ? blueprintName : DEFAULT_DATALAKE_BLUEPRINT;
    }

    private int determineInstanceCount(String key) {
        String instanceCount = testParameter.get(key);
        int instanceCountInt;
        try {
            instanceCountInt = Integer.parseInt(instanceCount);
        } catch (NumberFormatException e) {
            instanceCountInt = 1;
        }
        return instanceCountInt;
    }

}