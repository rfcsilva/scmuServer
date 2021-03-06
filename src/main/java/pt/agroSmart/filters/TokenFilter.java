package pt.agroSmart.filters;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;

import pt.agroSmart.resources.User.AuthToken;
import pt.agroSmart.resources.User.User;


/**
 *This filters filters all request that have a path pattern: "http://ip:port/rest/withtoken/*"
 *First it is checked if the JWT is authentic, in other words, it's verified.
 *In a second phase it checks if the JWT is registered in our system.
 *If both those conditions are verified, the the request is processed. It is blocked otherwise.
 *
 * @author Ruben Silva.
 *
 *
 */
@Provider
public class TokenFilter implements Filter {

    static final long ONE_MINUTE_IN_MILLIS=60000;//millisecs

    private static final Logger LOG = Logger.getLogger(TokenFilter.class.getName());

    public TokenFilter() {}


    /**
     * This method initializes the filter variables: the decryption algorithm and the JWT verifier.
     */
    @Override
    public void init(FilterConfig filterConfig) { } //nothing to be done


    /**
     * This method is responsible to filter the requests, the way it works is defined in the class documentation.
     */

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        boolean ok = true;
        DecodedJWT jwt = null;

        System.out.println("EHEHEH tou no filtro");
        
        /// Get the HTTP Authorization header from the request
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String authorizationHeader =   httpRequest.getHeader(HttpHeaders.AUTHORIZATION);

        if(authorizationHeader == null) {
            ok = false;
        }else {
            // Extract the stringToken from the HTTP Authorization header
            String stringToken = authorizationHeader.substring("Bearer".length()).trim();

            LOG.info("Verifing authentication stringToken...");
            try {

                jwt = AuthToken.verifier.verify(stringToken);
                LOG.info("Token verified.");

            }catch(TokenExpiredException expiredEx) {
                LOG.info("Token expired.");
                ok = false;


            }catch(Exception e) {
                LOG.info("Token not verified.");
                ok = false;
            }

            //Just to check that the stringToken is registered if not exception will be thrown
            String token_id = jwt.getId();
            String token_username = jwt.getClaim(User.USERNAME).asString();
            AuthToken token = new AuthToken(token_id, token_username, User.generateKey(token_username));

            try {

                token.ds_get();
                LOG.info("Token authentication 2...");

            }catch(Exception e) {
                LOG.info("Token Nao existe na datastore");
                LOG.info("Tentou encontrar o stringToken com id " + token_id );
                ok = false;
            }
        }

        if(ok) {
        	System.out.println("eita");
            chain.doFilter(request, response);
        }else {
        	System.out.println("Ah doido");
        	HttpServletResponse res = (HttpServletResponse) response;
            res.setStatus(401);
            res.sendRedirect("error/401/401.html");
        }
    }

    @Override
    public void destroy() { }
}