package org.zalando.github.zmon.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.social.github.api.impl.GitHubTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.zalando.github.zmon.security.IsAllowedOrgaSignupCondition;
import org.zalando.github.zmon.security.IsAllowedUserSignupCondition;
import org.zalando.zmon.security.AuthorityService;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Created by hjacobs on 14.12.15.
 */
public class GithubResourceServerTokenServices implements ResourceServerTokenServices {

    private AuthorityService authorityService;

    private final IsAllowedUserSignupCondition userCondition;
    private final IsAllowedOrgaSignupCondition orgaCondition;

    public GithubResourceServerTokenServices(AuthorityService authorityService, IsAllowedUserSignupCondition userCondition, IsAllowedOrgaSignupCondition orgaCondition) {
        this.authorityService = authorityService;
        this.userCondition = userCondition;
        this.orgaCondition = orgaCondition;
    }

    @Override
    public OAuth2Authentication loadAuthentication(String accessToken)
            throws AuthenticationException, InvalidTokenException {
        GitHubTemplate github = new GitHubTemplate(accessToken);

        try {
            boolean matchUser = userCondition.matches(github);
            boolean matchOrgas = orgaCondition.matches(github);

            if (!matchUser || !matchOrgas) {
                throw new InvalidTokenException("User or Organization access not allowed");
            }

        } catch (HttpClientErrorException ex) {
            if (HttpStatus.UNAUTHORIZED == ex.getStatusCode()) {
                throw new InvalidTokenException("Invalid GitHub access token!");
            }
            throw ex;
        }

        String userName = github.userOperations().getProfileId();

        Collection<? extends GrantedAuthority> authorities = authorityService.getAuthorities(userName);

        UsernamePasswordAuthenticationToken user = new UsernamePasswordAuthenticationToken(userName, "N/A",
                authorities);

        Set<String> scopes = Sets.newHashSet("uid");
        Map<String, Object> map = Maps.newHashMap();
        map.put("scopes", scopes);
        map.put("realm", "/employees");
        user.setDetails(map);

        OAuth2Request request = new OAuth2Request((Map) null, "NOT_NEEDED", (Collection) null, true, scopes, (Set) null,
                (String) null, (Set) null, (Map) null);
        return new OAuth2Authentication(request, user);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        throw new UnsupportedOperationException("Not supported: read access token");
    }
}
