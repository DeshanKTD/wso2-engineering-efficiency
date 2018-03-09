package org.wso2.deployer.filters;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.SignedJWT;
import org.apache.log4j.Logger;


import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class is for handling sso configuration.
 */
public class JWTAction implements Filter {
    private static final Logger logger = Logger.getLogger(JWTAction.class);

    /**
     * This method is for get public key
     *
     * @return return for getting public key
     * @throws IOException              if unable to load the file
     * @throws KeyStoreException        if unable to get instance
     * @throws CertificateException     if unable to certify
     * @throws NoSuchAlgorithmException cause by other underlying exceptions(KeyStoreException)
     */

    private static PublicKey getPublicKey() throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException {

        InputStream file = Thread.currentThread().getContextClassLoader().getResourceAsStream("Constant.KeyStore.KEYSTORE_FILE");
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        //loading key store with password
        keystore.load(file, "Constant.KeyStore.KEYSTORE_PASSWORD".toCharArray());
        Certificate cert = keystore.getCertificate(System.getenv("Constant.EnvironmentVariable.KEYSTORE_ALIAS"));
        return cert.getPublicKey();
    }

    public void init(FilterConfig filterConfig) {

    }


    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String jwt = request.getHeader("X-JWT-Assertion");
        String ssoRedirectUrl = System.getenv("Constant.EnvironmentVariable.CSE_CLIENT_SSO_URL");

        if (jwt == null || "".equals(jwt)) {
            logger.debug("Redirecting to {}");
            response.sendRedirect(ssoRedirectUrl);
            return;
        }

        String username;
        String roles;

        try {
            SignedJWT signedJWT = SignedJWT.parse(jwt);
            JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) getPublicKey());

            if (signedJWT.verify(verifier)) {
                logger.debug("JWT validation success for token: {}");
                username = signedJWT.getJWTClaimsSet().getClaim("http://wso2.org/claims/emailaddress").toString();
                roles = signedJWT.getJWTClaimsSet().getClaim("http://wso2.org/claims/role").toString();
//                logger.debug("User = {} | Roles = ", username, roles);
                logger.debug("User = {} | Roles = ");
            } else {
                logger.error("JWT validation failed for token: {}");
                response.sendRedirect(ssoRedirectUrl);
                return;
            }
        } catch (Exception e) {
//            logger.error("JWT validation failed for token: {}", jwt, e);
            logger.error("JWT validation failed for token: {}");
            response.sendRedirect(ssoRedirectUrl);
            return;
        }

        request.getSession().setAttribute("user", username);
        request.getSession().setAttribute("roles", roles);

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (ServletException e) {
            logger.error("Failed to pass the request, response objects through filters", e);
        }
    }

    public void destroy() {

    }
}