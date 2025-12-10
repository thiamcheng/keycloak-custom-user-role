package com.example.keycloak.db;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.models.*;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;

public class DbUserStorageProvider implements
        UserStorageProvider,
        UserLookupProvider,
        CredentialInputValidator {

    private final KeycloakSession session;
    private final ComponentModel model;

    public DbUserStorageProvider(KeycloakSession session, ComponentModel model) {
        this.session = session;
        this.model = model;
    }

    @Override
    public void close() {}

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        UserModel existing = session.users().getUserByUsername(realm, username);
        if (existing == null) return null;
        return new HardcodedUserAdapter(session, realm, model, existing);
    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        UserModel existing = session.users().getUserById(realm, id);
        if (existing == null) return null;
        return new HardcodedUserAdapter(session, realm, model, existing);
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        UserModel existing = session.users().getUserByEmail(realm, email);
        if (existing == null) return null;
        return new HardcodedUserAdapter(session, realm, model, existing);
    }

    // LDAP performs authentication
    @Override
    public boolean supportsCredentialType(String type) {
        return false;
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String type) {
        return false;
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput input) {
        return false;
    }
}

