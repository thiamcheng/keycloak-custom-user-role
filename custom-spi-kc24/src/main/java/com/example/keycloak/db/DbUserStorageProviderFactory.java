package com.example.keycloak.db;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class DbUserStorageProviderFactory implements UserStorageProviderFactory<DbUserStorageProvider> {

    public static final String PROVIDER_ID = "db-user-storage";

    @Override
    public DbUserStorageProvider create(KeycloakSession session, ComponentModel model) {
        return new DbUserStorageProvider(session, model);
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getHelpText() {
        return "User storage provider with per-username hardcoded roles.";
    }
}
