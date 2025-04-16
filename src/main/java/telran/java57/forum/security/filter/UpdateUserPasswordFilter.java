package telran.java57.forum.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Principal;

@Component
@Order(70)
public class UpdateUserPasswordFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if(checkEndpoint(request.getMethod(),request.getServletPath())){
            Principal principal = request.getUserPrincipal();
            String password = request.getHeader("X-Password");
            if(principal == null){
                response.sendError(401,"Unauthenticated");
                return;
            }
            if (password == null){
                response.sendError(400,"Missing Header");
                return;
            }
        }
        filterChain.doFilter(request,response);
    }

    private boolean checkEndpoint(String method, String servletPath) {
        return (HttpMethod.PUT.matches(method) && servletPath.matches("/account/password"));
    }
}
