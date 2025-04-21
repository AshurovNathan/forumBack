package telran.java57.forum.posts;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import telran.java57.forum.accounting.dao.UserAccountRepository;
import telran.java57.forum.accounting.model.UserAccount;
import telran.java57.forum.posts.dao.PostRepository;
import telran.java57.forum.posts.dto.NewCommentDto;
import telran.java57.forum.posts.dto.NewPostDto;
import telran.java57.forum.posts.dto.PeriodDto;
import telran.java57.forum.posts.dto.PostDto;
import telran.java57.forum.posts.model.Post;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


// To make these tests work, set the environment variable:
// MONGODB_URI={your_database_connection_string}
// It's recommended to use a separate test database, as these tests will delete all data from the specified database.
@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserAccountRepository userAccountRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void cleanMongoDB() {
        postRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    void testAddPost() throws Exception {
        userAccountRepository.save(new UserAccount("user", passwordEncoder.encode("1234"), "John", "Smith"));
        NewPostDto newPostDto = new NewPostDto("title", "content", Set.of("tag1", "tag2"));

        MockHttpServletRequestBuilder requestBuilder = post("/forum/post/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPostDto))
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:1234".getBytes()));;

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.author").value("user"))
                .andExpect(jsonPath("$.tags[0]").value("tag1"));
    }

    @Test
    void testFindPostById() throws Exception {
        userAccountRepository.save(new UserAccount("user", passwordEncoder.encode("1234"), "John", "Smith"));
        PostDto dto = new PostDto("1000", "title", "content", "user", LocalDateTime.now(), Set.of("tag1", "tag2"), 0, null);
        Post post = modelMapper.map(dto, Post.class);
        postRepository.save(post);

        MockHttpServletRequestBuilder requestBuilder = get("/forum/post/1000")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:1234".getBytes()));;

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1000"))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.author").value("user"))
                .andExpect(jsonPath("$.tags").isArray())
                .andExpect(jsonPath("$.tags[0]").value("tag1"))
                .andExpect(jsonPath("$.tags[1]").value("tag2"));
    }

    @Test
    void testFindPostByIdNotFound() throws Exception {
        userAccountRepository.save(new UserAccount("user", passwordEncoder.encode("1234"), "John", "Smith"));
        PostDto dto = new PostDto("1000", "title", "content", "user", LocalDateTime.now(), Set.of("tag1", "tag2"), 0, null);
        Post post = modelMapper.map(dto, Post.class);
        postRepository.save(post);

        MockHttpServletRequestBuilder requestBuilder = get("/forum/post/2000")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:1234".getBytes()));;

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdatePost() throws Exception {
        userAccountRepository.save(new UserAccount("user", passwordEncoder.encode("1234"), "John", "Smith"));
        PostDto dto = new PostDto("1000", "title", "content", "user", LocalDateTime.now(), Set.of("tag1"), 0, null);
        Post post = modelMapper.map(dto, Post.class);
        postRepository.save(post);

        NewPostDto updateDto = new NewPostDto("newTitle", "newContent", Set.of("tag2"));

        MockHttpServletRequestBuilder requestBuilder = put("/forum/post/1000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:1234".getBytes()));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("newTitle"))
                .andExpect(jsonPath("$.content").value("newContent"))
                .andExpect(jsonPath("$.tags",  containsInAnyOrder("tag1", "tag2")));
    }

    @Test
    void testUpdatePostOneOfThemNull() throws Exception {
        userAccountRepository.save(new UserAccount("user", passwordEncoder.encode("1234"), "John", "Smith"));
        PostDto dto = new PostDto("1000", "title", "content", "user", LocalDateTime.now(), Set.of("tag1"), 0, null);
        Post post = modelMapper.map(dto, Post.class);
        postRepository.save(post);

        NewPostDto updateDto = new NewPostDto(null, "newContent", null);

        MockHttpServletRequestBuilder requestBuilder = put("/forum/post/1000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:1234".getBytes()));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("newContent"))
                .andExpect(jsonPath("$.tags",  containsInAnyOrder("tag1")));
    }

    @Test
    void testUpdatePostNotFound() throws Exception {
        userAccountRepository.save(new UserAccount("user", passwordEncoder.encode("1234"), "John", "Smith"));
        PostDto dto = new PostDto("1000", "title", "content", "user", LocalDateTime.now(), Set.of("tag1"), 0, null);
        Post post = modelMapper.map(dto, Post.class);
        postRepository.save(post);

        NewPostDto updateDto = new NewPostDto("newTitle", "newContent", Set.of("tag2"));

        MockHttpServletRequestBuilder requestBuilder = put("/forum/post/2000")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto))
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:1234".getBytes()));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeletePost() throws Exception {
        userAccountRepository.save(new UserAccount("user", passwordEncoder.encode("1234"), "John", "Smith"));
        PostDto dto = new PostDto("1000", "title", "content", "user", LocalDateTime.now(), Set.of("tag1"), 0, null);
        Post post = modelMapper.map(dto, Post.class);
        postRepository.save(post);

        MockHttpServletRequestBuilder requestBuilder = delete("/forum/post/1000")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:1234".getBytes()));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.tags",  containsInAnyOrder("tag1")));
    }

    @Test
    void testDeletePostNotFound() throws Exception {
        userAccountRepository.save(new UserAccount("user", passwordEncoder.encode("1234"), "John", "Smith"));
        PostDto dto = new PostDto("1000", "title", "content", "user", LocalDateTime.now(), Set.of("tag1"), 0, null);
        Post post = modelMapper.map(dto, Post.class);
        postRepository.save(post);

        MockHttpServletRequestBuilder requestBuilder = delete("/forum/post/2000")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:1234".getBytes()));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isForbidden());
    }

    @Test
    void testFindPostsByAuthor() throws Exception {
        userAccountRepository.save(new UserAccount("user", passwordEncoder.encode("1234"), "John", "Smith"));
        PostDto dto = new PostDto("1000", "title", "content", "user", LocalDateTime.now(), Set.of("tag1"), 0, null);
        Post post = modelMapper.map(dto, Post.class);
        postRepository.save(post);
        PostDto dto2 = new PostDto("2000", "title2", "content2", "user", LocalDateTime.now(), Set.of("tag1"), 0, null);
        Post post2 = modelMapper.map(dto2, Post.class);
        postRepository.save(post2);

        MockHttpServletRequestBuilder requestBuilder = get("/forum/posts/author/user")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:1234".getBytes()));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].author").value("user"))
                .andExpect(jsonPath("$[1].author").value("user"));
    }

    @Test
    void testAddComment() throws Exception {
        userAccountRepository.save(new UserAccount("user", passwordEncoder.encode("1234"), "John", "Smith"));
        PostDto dto = new PostDto("1000", "title", "content", "user", LocalDateTime.now(), Set.of("tag1"), 0, null);
        Post post = modelMapper.map(dto, Post.class);
        postRepository.save(post);

        NewCommentDto commentDto = new NewCommentDto("Nice post!");

        MockHttpServletRequestBuilder requestBuilder = put("/forum/post/1000/comment/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto))
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:1234".getBytes()));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comments.length()").value(1))
                .andExpect(jsonPath("$.comments[0].message").value("Nice post!"))
                .andExpect(jsonPath("$.comments[0].user").value("user"));
    }

    @Test
    void testAddCommentNotFound() throws Exception {
        userAccountRepository.save(new UserAccount("user", passwordEncoder.encode("1234"), "John", "Smith"));
        PostDto dto = new PostDto("1000", "title", "content", "user", LocalDateTime.now(), Set.of("tag1"), 0, null);
        Post post = modelMapper.map(dto, Post.class);
        postRepository.save(post);

        NewCommentDto commentDto = new NewCommentDto("Nice post!");

        MockHttpServletRequestBuilder requestBuilder = put("/forum/post/2000/comment/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto))
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:1234".getBytes()));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void testFindPostsByTags() throws Exception {
        userAccountRepository.save(new UserAccount("user", passwordEncoder.encode("1234"), "John", "Smith"));
        PostDto dto = new PostDto("1000", "title", "content", "user", LocalDateTime.now(), Set.of("tag1","t2"), 0, null);
        Post post = modelMapper.map(dto, Post.class);
        postRepository.save(post);
        PostDto dto2 = new PostDto("2000", "title2", "content2", "user", LocalDateTime.now(), Set.of("tag1","t2"), 0, null);
        Post post2 = modelMapper.map(dto2, Post.class);
        postRepository.save(post2);

        MockHttpServletRequestBuilder requestBuilder = post("/forum/posts/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of("tag1", "t2")))
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:1234".getBytes()));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].tags", containsInAnyOrder("tag1", "t2")))
                .andExpect(jsonPath("$[1].tags", containsInAnyOrder("tag1", "t2")));
    }

    @Test
    void testFindPostsByPeriod() throws Exception {
        userAccountRepository.save(new UserAccount("user", passwordEncoder.encode("1234"), "John", "Smith"));
        PostDto dto = new PostDto("1000", "title", "content", "user", LocalDateTime.of(2023, 7, 1,0,0), Set.of("tag1"), 0, null);
        Post post = modelMapper.map(dto, Post.class);
        postRepository.save(post);
        PostDto dto2 = new PostDto("2000", "title2", "content2", "user", LocalDateTime.of(2023, 4, 2,0,0), Set.of("tag1"), 0, null);
        Post post2 = modelMapper.map(dto2, Post.class);
        postRepository.save(post2);

        LocalDateTime from = LocalDateTime.of(2023, 1, 1, 0, 0);
        LocalDateTime to = LocalDateTime.of(2023, 12, 31, 23, 59);
        PeriodDto periodDto = new PeriodDto(from, to);

        MockHttpServletRequestBuilder requestBuilder = post("/forum/posts/period")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(periodDto))
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:1234".getBytes()));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("1000"))
                .andExpect(jsonPath("$[1].id").value("2000"));
    }

    @Test
    void addLike() throws Exception {
        userAccountRepository.save(new UserAccount("user", passwordEncoder.encode("1234"), "John", "Smith"));
        PostDto dto = new PostDto("1000", "title", "content", "user", LocalDateTime.now(), Set.of("tag1"), 0, null);
        Post post = modelMapper.map(dto, Post.class);
        postRepository.save(post);

        MockHttpServletRequestBuilder requestBuilder = put("/forum/post/1000/like")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString("user:1234".getBytes()));

        mockMvc.perform(requestBuilder)
                .andExpect(status().isOk());

        Optional<Post> updated = postRepository.findById("1000");
        assertTrue(updated.isPresent());
        assertEquals(1, updated.get().getLikes());
    }
}