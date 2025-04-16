package telran.java57.forum.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import telran.java57.forum.accounting.model.Role;
import telran.java57.forum.posts.dao.PostRepository;
import telran.java57.forum.security.filter.model.User;

import java.io.IOException;

@Component
@Order(60)
@AllArgsConstructor
public class UpdatePostFilter implements Filter {
    final PostRepository postRepository;
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if(checkEndpoint(request.getMethod(),request.getServletPath())){
            User user = (User) request.getUserPrincipal();
            String[] parts = request.getServletPath().split("/");
            String postId = parts[parts.length - 1];
            String owner = postRepository.findById(postId).get().getAuthor();
            if(!(user.getName().equalsIgnoreCase(owner))){
                response.sendError(403,"Permission denied");
                return;
            }
        }
        filterChain.doFilter(request,response);
    }

    private boolean checkEndpoint(String method, String servletPath) {
        return (HttpMethod.PUT.matches(method) && servletPath.matches("/forum/post/\\w+"));
    }
}
