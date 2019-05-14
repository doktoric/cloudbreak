package com.sequenceiq.cloudbreak.controller.validation.environment.network;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sequenceiq.cloudbreak.api.endpoint.v4.environment.base.EnvironmentNetworkAwsV4Params;
import com.sequenceiq.cloudbreak.api.endpoint.v4.environment.requests.EnvironmentNetworkV4Request;
import com.sequenceiq.cloudbreak.controller.validation.ValidationResult;

@RunWith(MockitoJUnitRunner.class)
public class AwsEnvironmentNetworkValidatorTest {

    public static final String NETWORK_CIDR = "10.0.0.0/16";

    public static final Set<String> SUBNET_CIDRS = Set.of("10.0.1.0/24", "10.0.2.0/24");

    public static final Set<String> SUBNET_IDS = Set.of("subnet-1", "subnet-2");

    public static final String VPC_ID = "vpc-id";

    private static final Logger LOGGER = LoggerFactory.getLogger(AwsEnvironmentNetworkValidatorTest.class);

    @InjectMocks
    private AwsEnvironmentNetworkValidator underTest;

    @Mock
    private NetworkCidrValidator networkCidrValidator;

    private ValidationResult.ValidationResultBuilder validationResult;

    private EnvironmentNetworkV4Request networkV4Request;

    @Before
    public void before() {
        validationResult = new ValidationResult.ValidationResultBuilder();
        networkV4Request = new EnvironmentNetworkV4Request();
    }

    @Test
    public void testValidateShouldReturnsWithErrorWhenTheEnvironmentNetworkV4RequestIsNull() {
        underTest.validate(null, validationResult);

        verifyZeroInteractions(networkCidrValidator);
        assertTrue(validationResult.build().hasError());
        assertTrue(validationResult.build().getErrors().size() == 1);
        LOGGER.info(validationResult.build().getFormattedErrors());
    }

    @Test
    public void testValidateShouldReturnsWithoutErrorsWhenAllParameterValidInCaseOfNewNetwork() {
        networkV4Request.setNetworkCidr(NETWORK_CIDR);
        networkV4Request.setSubnetCidrs(SUBNET_CIDRS);
        doNothing().when(networkCidrValidator).validate(networkV4Request, validationResult);

        underTest.validate(networkV4Request, validationResult);

        verify(networkCidrValidator).validate(networkV4Request, validationResult);
        assertFalse(validationResult.build().hasError());
    }

    @Test
    public void testValidateShouldReturnsWithoutErrorsWhenAllParameterValidInCaseOfExistingNetwork() {
        networkV4Request.setSubnetIds(SUBNET_IDS);
        networkV4Request.setAws(awsVpc());

        underTest.validate(networkV4Request, validationResult);

        verify(networkCidrValidator).validate(networkV4Request, validationResult);
        assertFalse(validationResult.build().hasError());
    }

    @Test
    public void testValidateShouldReturnsWithOutErrorWhenTheSubnetCidrIsNotPresentInCaseOfExistingNetwork() {
        networkV4Request.setNetworkCidr(NETWORK_CIDR);
        networkV4Request.setSubnetIds(SUBNET_IDS);
        networkV4Request.setAws(awsVpc());

        underTest.validate(networkV4Request, validationResult);

        verify(networkCidrValidator).validate(networkV4Request, validationResult);
        assertFalse(validationResult.build().hasError());
    }

    @Test
    public void testValidateShouldReturnsWithOutErrorWhenTheNetworkCidrIsNotPresentInCaseOfExistingNetwork() {
        networkV4Request.setSubnetCidrs(SUBNET_CIDRS);
        networkV4Request.setSubnetIds(SUBNET_IDS);
        networkV4Request.setAws(awsVpc());

        underTest.validate(networkV4Request, validationResult);

        verify(networkCidrValidator).validate(networkV4Request, validationResult);
        assertFalse(validationResult.build().hasError());
    }

    @Test
    public void testValidateShouldReturnsWithOutErrorWhenTheVpcIdIsNotPresentInCaseOfNewNetwork() {
        networkV4Request.setNetworkCidr(NETWORK_CIDR);
        networkV4Request.setSubnetCidrs(SUBNET_CIDRS);
        networkV4Request.setSubnetIds(SUBNET_IDS);
        doNothing().when(networkCidrValidator).validate(networkV4Request, validationResult);

        underTest.validate(networkV4Request, validationResult);

        verify(networkCidrValidator).validate(networkV4Request, validationResult);
        assertFalse(validationResult.build().hasError());
    }

    @Test
    public void testValidateShouldReturnsWithOutErrorWhenTheSubnetIdIsNotPresentInCaseOfNewNetwork() {
        networkV4Request.setAws(awsVpc());
        networkV4Request.setNetworkCidr(NETWORK_CIDR);
        networkV4Request.setSubnetCidrs(SUBNET_CIDRS);
        doNothing().when(networkCidrValidator).validate(networkV4Request, validationResult);

        underTest.validate(networkV4Request, validationResult);

        verify(networkCidrValidator).validate(networkV4Request, validationResult);
        assertFalse(validationResult.build().hasError());
    }

    @Test
    public void testValidateShouldReturnsWithErrorWhenTheNetworkCidrIsNotValidInCaseOfNewNetwork() {
        networkV4Request.setNetworkCidr(NETWORK_CIDR);
        networkV4Request.setSubnetCidrs(SUBNET_CIDRS);
        validationResult.error("Cidr validation error.");
        doNothing().when(networkCidrValidator).validate(networkV4Request, validationResult);

        underTest.validate(networkV4Request, validationResult);

        verify(networkCidrValidator).validate(networkV4Request, validationResult);
        assertTrue(validationResult.build().hasError());
        assertTrue(validationResult.build().getErrors().size() == 1);
        LOGGER.info(validationResult.build().getFormattedErrors());
    }

    @Test
    public void testValidateShouldReturnsWithErrorsWhenTheVpcIdAndNetworkCidrAreNotPresent() {
        networkV4Request.setSubnetCidrs(SUBNET_CIDRS);
        networkV4Request.setSubnetIds(SUBNET_IDS);

        underTest.validate(networkV4Request, validationResult);

        verify(networkCidrValidator).validate(networkV4Request, validationResult);
        assertTrue(validationResult.build().hasError());
        assertTrue(validationResult.build().getErrors().size() == 2);
        LOGGER.info(validationResult.build().getFormattedErrors());
    }

    @Test
    public void testValidateShouldReturnsWithErrorsWhenAllParameterIsMissing() {
        underTest.validate(networkV4Request, validationResult);

        verify(networkCidrValidator).validate(networkV4Request, validationResult);
        assertTrue(validationResult.build().hasError());
        assertTrue(validationResult.build().getErrors().size() == 4);
        LOGGER.info(validationResult.build().getFormattedErrors());
    }

    private EnvironmentNetworkAwsV4Params awsVpc() {
        EnvironmentNetworkAwsV4Params awsV4Params = new EnvironmentNetworkAwsV4Params();
        awsV4Params.setVpcId(VPC_ID);
        return awsV4Params;
    }

}