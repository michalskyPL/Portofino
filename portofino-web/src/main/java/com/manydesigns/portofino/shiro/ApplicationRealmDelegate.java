/*
 * Copyright (C) 2005-2012 ManyDesigns srl.  All rights reserved.
 * http://www.manydesigns.com/
 *
 * Unless you have purchased a commercial license agreement from ManyDesigns srl,
 * the following license terms apply:
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.manydesigns.portofino.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;

import java.util.Set;

/**
 * Delegate interface used by Portofino's default Shiro realm implementation.
 * Key functionality is delegated to instances of this interface to allow
 * arbitrary implementations by users (security.groovy) without touching the core framework.
 *
 * @author Paolo Predonzani     - paolo.predonzani@manydesigns.com
 * @author Angelo Lupo          - angelo.lupo@manydesigns.com
 * @author Giampiero Granatella - giampiero.granatella@manydesigns.com
 * @author Alessio Stalla       - alessio.stalla@manydesigns.com
 */
public interface ApplicationRealmDelegate {
    public static final String copyright =
            "Copyright (c) 2005-2012, ManyDesigns srl";

    /**
     * Returns the AuthorizationInfo given a user. Within Portofino, this means the list of groups
     * associated with the user.
     * @param realm the ApplicationRealm that is calling this method.
     * @param userId the user id object. Its actual meaning depends on the implementation; it is the user's primary
     * principal, i.e. the string that's normally used as a username to log in.
     * @return an AuthorizationInfo object containing the roles (groups) of the user.
     */
    AuthorizationInfo getAuthorizationInfo(ApplicationRealm realm, Object userId);

    /**
     * Performs authentication. The token can be of different types, depending on the realm.
     * @param realm the ApplicationRealm that is calling this method.
     * @param token the authentication token. Typical examples are a UsernamePasswordToken or a OpenIDToken.
     * @return an AuthenticationInfo object if login was successful.
     * @throws org.apache.shiro.authc.AuthenticationException if authentication failed.
     */
    AuthenticationInfo getAuthenticationInfo(ApplicationRealm realm, AuthenticationToken token);

    /**
     * Returns the list of users known to the system. This is used by the framework when presenting a list of
     * possible users, e.g. when configuring permissions for a page.
     * @param realm the ApplicationRealm that is calling this method.
     * @return a set of users, represented as strings (the usernames used to log in).
     */
    Set<String> getUsers(ApplicationRealm realm);

    /**
     * Returns the list of groups known to the system. This is used by the framework when presenting a list of
     * possible groups, e.g. when configuring permissions for a page.
     * @param realm the ApplicationRealm that is calling this method.
     * @return a set of groups.
     */
    Set<String> getGroups(ApplicationRealm realm);
}
