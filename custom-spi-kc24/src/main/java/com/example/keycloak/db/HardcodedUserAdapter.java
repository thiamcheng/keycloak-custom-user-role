package com.example.keycloak.db;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.*;
import org.keycloak.storage.adapter.AbstractUserAdapter;

import java.util.*;

public class HardcodedUserAdapter extends AbstractUserAdapter {

    private final String username;

    private static final Map<String, List<String>> ROLE_MAP = Map.of(
        "alice", List.of("aml_viewer", "aml_editor"),
        "bob", List.of("aml_viewer"),
        "charlie", List.of("aml_approver")
    );

    public HardcodedUserAdapter(KeycloakSession session,
                                RealmModel realm,
                                ComponentModel model,
                                String username) {
        super(session, realm, model);
        this.username = username;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public UserCredentialManager credentialManager() {
        return session.userCredentialManager();
    }

    @Override
    public Set<RoleModel> getRealmRoleMappings() {
        List<String> roles = ROLE_MAP.getOrDefault(username, List.of("aml_basic"));
        Set<RoleModel> out = new HashSet<>();

        for (String rname : roles) {
            RoleModel role = realm.getRole(rname);
            if (role == null) {
                role = realm.addRole(rname);
            }
            out.add(role);
        }
        return out;
    }

    @Override
    public Set<RoleModel> getRoleMappings() {
        return getRealmRoleMappings();
    }

    @Override
    public boolean hasRealmRole(RoleModel role) {
        return getRealmRoleMappings().contains(role);
    }

    @Override
    public String getEmail() {
        return username + "@example.com";
    }
}

