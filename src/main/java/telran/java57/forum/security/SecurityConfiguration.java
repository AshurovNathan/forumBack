package telran.java57.forum.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import telran.java57.forum.posts.dao.PostRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, PostRepository postRepository) throws Exception {
        http.httpBasic(Customizer.withDefaults());
        http.csrf(csrf -> csrf.disable());
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.POST,"/account/register").permitAll()
                .requestMatchers(HttpMethod.DELETE,"/account/user/{login}")
                .access(new WebExpressionAuthorizationManager("hasRole('ADMINISTRATOR') or authentication.name == #login"))
                .requestMatchers(HttpMethod.PUT,"/account/user/{login}")
                .access(new WebExpressionAuthorizationManager("authentication.name == #login"))
                .requestMatchers(HttpMethod.PUT,"/account/user/{login}/role/{role}")
                .access(new WebExpressionAuthorizationManager("hasRole('ADMINISTRATOR')"))
                .requestMatchers(HttpMethod.DELETE,"/account/user/{login}/role/{role}")
                .access(new WebExpressionAuthorizationManager("hasRole('ADMINISTRATOR')"))
                .requestMatchers(HttpMethod.PUT,"/account/password")
                .access(new WebExpressionAuthorizationManager("isAuthenticated()"))
                .requestMatchers(HttpMethod.POST,"/forum/post/{author}")
                .access(new WebExpressionAuthorizationManager("authentication.name == #author"))
                .requestMatchers(HttpMethod.DELETE, "/forum/post/{postId}")
                .access((auth, context) -> {
                    HttpServletRequest request = context.getRequest();
                    String[] parts = request.getRequestURI().split("/");
                    String postId = parts[parts.length - 1];
                    boolean hasModeratorRole = auth.get().getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_MODERATOR"));
                    return postRepository.findById(postId)
                            .map(post -> post.getAuthor().equals(auth.get().getName()) || hasModeratorRole)
                            .map(AuthorizationDecision::new)
                            .orElse(new AuthorizationDecision(hasModeratorRole));
                })
                .requestMatchers(HttpMethod.PUT, "/forum/post/{postId}")
                .access((auth, context) -> {
                    HttpServletRequest request = context.getRequest();
                    String[] parts = request.getRequestURI().split("/");
                    String postId = parts[parts.length - 1];
                    return postRepository.findById(postId).stream()
                            .map(p -> p.getAuthor().equals(auth.get().getName()))
                            .map(AuthorizationDecision::new)
                            .findAny().orElse(new AuthorizationDecision(false));
                })
                .requestMatchers(HttpMethod.PUT,"/forum/post/{id}/comment/{author}")
                .access(new WebExpressionAuthorizationManager("authentication.name == #author"))
                .requestMatchers(HttpMethod.POST, "/forum/posts/")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/forum/posts/")
                .permitAll()
                .requestMatchers(HttpMethod.POST,"forum/posts/period")
                .permitAll()
                .anyRequest().authenticated());
        return http.build();
    }
}
