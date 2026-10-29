package ZooCapDelivery.ZooCapDelivery.Filter;

import ZooCapDelivery.ZooCapDelivery.Service.UserDeatilsService;
import ZooCapDelivery.ZooCapDelivery.Utility.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private UserDeatilsService userDeatilsService;
    @Autowired
    private JwtUtil jwtUtil;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
           String authorizationHeader = request.getHeader("Authorization");
           String username  = null;
           String jwt = null;
           if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
               jwt = authorizationHeader.substring(7);
               username = jwtUtil.extraxtUsername(jwt);
           }
           if(username != null){
               UserDetails userDeatils = userDeatilsService.loadUserByUsername(username);
               System.out.println(
                       userDeatils.getAuthorities()
               );
               if(jwtUtil.validateToken(jwt)){
                  UsernamePasswordAuthenticationToken authenticationToken = new
                          UsernamePasswordAuthenticationToken(userDeatils, null
                  ,userDeatils.getAuthorities());
                  authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                 SecurityContextHolder.getContext().setAuthentication(authenticationToken);
               }
           }
           filterChain.doFilter(request, response);
    }
}
