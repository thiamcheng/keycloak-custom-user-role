package com.example.keycloak.aml;

import org.keycloak.models.ClientSessionContext;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.ProtocolMapperModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.UserSessionModel;
import org.keycloak.protocol.oidc.OIDCLoginProtocol;
import org.keycloak.protocol.oidc.mappers.AbstractOIDCProtocolMapper;
import org.keycloak.protocol.oidc.mappers.OIDCAccessTokenMapper;
import org.keycloak.protocol.oidc.mappers.OIDCIDTokenMapper;
import org.keycloak.protocol.oidc.mappers.UserInfoTokenMapper;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;

import java.util.*;

public class HardcodedAmlRolesProtocolMapper extends AbstractOIDCProtocolMapper
        implements OIDCAccessTokenMapper, OIDCIDTokenMapper, UserInfoTokenMapper {

    public static final String PROVIDER_ID = "aml-hardcoded-roles-mapper";

    private static final Map<String, Set<String>> ROLE_MAP = Map.of(
            "alice", Set.of("aml_viewer", "aml_approver"),
            "bob", Set.of("aml_viewer"),
            "charlie", Set.of("aml_checker")
    );

    private static final List<ProviderConfigProperty> CONFIG_PROPERTIES = new ArrayList<>();

    @Override
    public String getDisplayCategory() {
        return "Token mapper";
    }

    @Override
    public String getDisplayType() {
        return "Hardcoded AML roles by username";
    }

    @Override
    public String getHelpText() {
        return "Adds AML roles based on username from LDAP into realm_access and aml_roles claim.";
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return CONFIG_PROPERTIES;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    // Required but unused
    @Override
    protected void setClaim(IDToken token, ProtocolMapperModel mappingModel,
                            UserSessionModel userSession, KeycloakSession session,
                            ClientSessionContext clientSessionCtx) {
    }

    /**
     * Keycloak 24 requires: AccessToken return type.
     */
    @Override
    public AccessToken transformAccessToken(AccessToken token,
                                            ProtocolMapperModel mappingModel,
                                            KeycloakSession session,
                                            UserSessionModel userSession,
                                            ClientSessionContext clientSessionCtx) {

        if (userSession == null) return token;

        UserModel user = userSession.getUser();
        if (user == null) return token;

        String username = user.getUsername();
        Set<String> roles = ROLE_MAP.get(username);

        if (roles == null || roles.isEmpty()) return token;

        // Add aml_roles claim
        token.getOtherClaims().put("aml_roles", new ArrayList<>(roles));

        // Add to realm_access.roles
        AccessToken.Access access = token.getRealmAccess();
        if (access == null) access = new AccessToken.Access();

        roles.forEach(access::addRole);
        token.setRealmAccess(access);

        return token;
    }

    public static ProtocolMapperModel create(String name) {
        ProtocolMapperModel mapper = new ProtocolMapperModel();
        mapper.setName(name);
        mapper.setProtocolMapper(PROVIDER_ID);
        mapper.setProtocol(OIDCLoginProtocol.LOGIN_PROTOCOL);
        return mapper;
    }
}

