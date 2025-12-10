package com.example.keycloak.db;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.PasswordCredentialModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;

import java.util.List;
import java.util.Map;

public class DbUserStorageProvider implements
        UserStorageProvider,
        UserLookupProvider,
        UserQueryProvider,
        CredentialInputValidator {

    private final KeycloakSession session;
    private final ComponentModel model;

    public DbUserStorageProvider(KeycloakSession session, ComponentModel model) {
        this.session = session;
        this.model = model;
    }

    // ============= Lookup Methods =============

    @Override
    public UserModel getUserById(String id, RealmModel realm) {
        String externalId = id.substring(id.indexOf(':') + 1);
        return loadUser(externalId, realm);
    }

    @Override
    public UserModel getUserByUsername(String username, RealmModel realm) {
        return loadUser(username, realm);
    }

    @Override
    public UserModel getUserByEmail(String email, RealmModel realm) {
        return null;
    }

    // ============= Required Query Methods =============

    @Override
    public List<UserModel> getUsers(RealmModel realm) {
        return List.of();
    }

    @Override
    public List<UserModel> getUsers(RealmModel realm, int first, int max) {
        return List.of();
    }

    @Override
    public int getUsersCount(RealmModel realm) {
        return 0;
    }

    @Override
    public List<UserModel> searchForUser(Map<String, String> params, RealmModel realm) {
        return List.of();
    }

    @Override
    public List<UserModel> searchForUser(String search, RealmModel realm) {
        return List.of();
    }

    // ============= Credential Validation =============

    @Override
    public boolean supportsCredentialType(String credentialType) {
        return PasswordCredentialModel.TYPE.equals(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        return true; // LDAP handles password
    }

    @Override
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
        return true; // Always valid (we rely on LDAP)
    }

    // ============= Load User Adapter =============

    private UserModel loadUser(String username, RealmModel realm) {
        return new HardcodedUserAdapter(session, realm, model, username);
    }

    @Override
    public void close() { }
}

