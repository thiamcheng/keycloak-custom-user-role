package com.example.keycloak.db;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.*;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;

import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

public class HardcodedUserAdapter extends AbstractUserAdapterFederatedStorage {

    private final UserModel delegate;

    public HardcodedUserAdapter(KeycloakSession session,
                                RealmModel realm,
                                ComponentModel model,
                                UserModel delegate) {
        super(session, realm, model);
        this.delegate = delegate;
        this.storageId = new StorageId(model.getId(), delegate.getId());
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public String getUsername() {
        return delegate.getUsername();
    }

    @Override
    public void setUsername(String username) {
        delegate.setUsername(username);
    }

    @Override
    public String getEmail() {
        return delegate.getEmail();
    }

    @Override
    public void setEmail(String email) {
        delegate.setEmail(email);
    }

    @Override
    public String getFirstName() {
        return delegate.getFirstName();
    }

    @Override
    public void setFirstName(String firstName) {
        delegate.setFirstName(firstName);
    }

    @Override
    public String getLastName() {
        return delegate.getLastName();
    }

    @Override
    public void setLastName(String lastName) {
        delegate.setLastName(lastName);
    }

    @Override
    public boolean isEnabled() {
        return delegate.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        delegate.setEnabled(enabled);
    }

    /**
     * KC 24: Add roles by overriding getRoleMappingsStream()
     */
    @Override
    public java.util.stream.Stream<RoleModel> getRoleMappingsStream() {

        Set<RoleModel> roles = delegate.getRoleMappingsStream()
                .collect(Collectors.toSet());

        String username = delegate.getUsername();
        if (username != null) {
            switch (username.toLowerCase()) {
                case "alice":
                    addRoles(roles, "AML_VIEWER", "AML_MAKER");
                    break;
                case "bob":
                    addRoles(roles, "AML_APPROVER");
                    break;
                case "charlie":
                    addRoles(roles, "AML_VIEWER", "AML_MAKER", "AML_APPROVER");
                    break;
                default:
                    addRoles(roles, "AML_VIEWER");
            }
        }

        return roles.stream();
    }

    private void addRoles(Set<RoleModel> roles, String... names) {
        for (String name : names) {
            RoleModel r = realm.getRole(name);
            if (r == null) r = realm.addRole(name);
            roles.add(r);
        }
    }
}

