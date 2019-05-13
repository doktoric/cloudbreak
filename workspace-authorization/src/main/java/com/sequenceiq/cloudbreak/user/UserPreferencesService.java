package com.sequenceiq.cloudbreak.user;

import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sequenceiq.cloudbreak.common.service.TransactionService;
import com.sequenceiq.cloudbreak.common.service.TransactionService.TransactionExecutionException;
import com.sequenceiq.cloudbreak.common.service.TransactionService.TransactionRuntimeExecutionException;
import com.sequenceiq.cloudbreak.util.ThrowableUtil;
import com.sequenceiq.cloudbreak.workspace.model.User;
import com.sequenceiq.cloudbreak.workspace.model.UserPreferences;
import com.sequenceiq.cloudbreak.workspace.repository.workspace.UserPreferencesRepository;

@Service
public class UserPreferencesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserPreferencesService.class);

    @Inject
    private UserPreferencesRepository userPreferencesRepository;

    @Inject
    private UserService userService;

    @Inject
    private TransactionService transactionService;

    public Optional<UserPreferences> getByUserId(String userId) {
        User user = userService.getByUserId(userId)
                .orElseThrow(ThrowableUtil.notFound("User", userId));
        return getByUser(user);
    }

    public UserPreferences getWithExternalId(User user) {
        LOGGER.debug("Get or create user preferences with external id for user '{}'", user.getUserId());
        UserPreferences result;
        Optional<UserPreferences> userOptional = getByUser(user);
        if (userOptional.isPresent()) {
            result = userOptional.get();
            if (StringUtils.isEmpty(result.getExternalId())) {
                result.setExternalId(generateExternalId());
                result = update(result);
            }
            return result;
        }
        throw new NotFoundException(String.format("User preferences could not be found for user that has id '%s'.", user.getUserId()));
    }

    public UserPreferences save(UserPreferences userPreferences) {
        return userPreferencesRepository.save(userPreferences);
    }

    private Optional<UserPreferences> getByUser(User user) {
        LOGGER.debug("Get user preferences for user '{}'", user.getUserId());
        return userPreferencesRepository.findByUser(user);
    }

    private String generateExternalId() {
        return UUID.randomUUID().toString();
    }

    private UserPreferences update(UserPreferences userPreferences) {
        User user = userPreferences.getUser();
        LOGGER.debug("Update user preferences for user '{}'", user.getUserId());
        try {
            return transactionService.required(() -> userPreferencesRepository.save(userPreferences));
        } catch (TransactionExecutionException e) {
            LOGGER.warn("UserPreferences could not be updated. ", e);
            throw new TransactionRuntimeExecutionException(e);
        }
    }

}
