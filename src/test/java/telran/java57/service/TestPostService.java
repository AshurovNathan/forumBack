package telran.java57.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import telran.java57.forum.posts.dao.PostRepository;
import telran.java57.forum.posts.dto.*;
import telran.java57.forum.posts.dto.exception.PostNotFoundException;
import telran.java57.forum.posts.model.Post;
import telran.java57.forum.posts.service.PostServiceImpl;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestPostService {
    @Mock
    ModelMapper modelMapper;

    @Mock
    PostRepository postRepository;

    @InjectMocks
    PostServiceImpl postService;

    @Test
    void testAddNewPost(){
        String author = "John";
        NewPostDto newPostDto = new NewPostDto("title","content", Set.of("tag1"));
        Post post = new Post(newPostDto.getTitle(),newPostDto.getContent(),author,newPostDto.getTags());
        PostDto postDto = new PostDto("1000", "title","content","user", LocalDateTime.now(),Set.of("tag1"),0,null);

        when(postRepository.save(post)).thenReturn(post);
        when(modelMapper.map(post, PostDto.class)).thenReturn(postDto);

        PostDto res = postService.addNewPost(author,newPostDto);
        assertNotNull(res);

        assertEquals(postDto.getId(),res.getId());

        verify(postRepository).save(post);
        verify(modelMapper).map(post,PostDto.class);
    }

    @Test
    void findPostById(){
        String postId = "1000";
        PostDto postDto = new PostDto("1000", "title","content","user", LocalDateTime.now(),Set.of("tag1"),0,null);
        Post post = new Post("title","content","author",Set.of("tag1"));

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(modelMapper.map(post,PostDto.class)).thenReturn(postDto);

        PostDto res = postService.findPostById(postId);
        assertNotNull(res);

        assertEquals(postDto.getId(),res.getId());

        verify(postRepository).findById(postId);
        verify(modelMapper).map(post,PostDto.class);
    }

    @Test
    void findPostByIdNotFound(){
        String postId = "0";

        when(postRepository.findById(postId)).thenReturn(Optional.empty());
        assertThrows(PostNotFoundException.class,() -> postService.findPostById(postId));

        verify(postRepository).findById(postId);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testUpdatePost(){
        String postId = "1000";
        NewPostDto newPostDto = new NewPostDto("ti", "con", new HashSet<>(Set.of("t1")));
        Post post = new Post("title", "content", "user", new HashSet<>(Set.of("tag1", "tag2")));
        PostDto postDto = new PostDto(postId, "ti", "con", "user", LocalDateTime.now(),
                new HashSet<>(Set.of("tag1", "tag2", "t1")), 0, null);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);
        when(modelMapper.map(post,PostDto.class)).thenReturn(postDto);

        PostDto res = postService.updatePost(postId,newPostDto);
        assertNotNull(res);

        assertEquals(postDto.getTitle(),res.getTitle());
        assertEquals(postDto.getTags(),res.getTags());

        verify(postRepository).findById(postId);
        verify(postRepository).save(post);
        verify(modelMapper).map(post,PostDto.class);
    }

    @Test
    void testUpdatePostNull(){
        String postId = "1000";
        NewPostDto newPostDto = new NewPostDto("ti", "con", null);
        Post post = new Post("title", "content", "user", new HashSet<>(Set.of("tag1", "tag2")));
        PostDto postDto = new PostDto(postId, "ti", "con", "user", LocalDateTime.now(),
                new HashSet<>(Set.of("tag1", "tag2")), 0, null);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);
        when(modelMapper.map(post,PostDto.class)).thenReturn(postDto);

        PostDto res = postService.updatePost(postId,newPostDto);
        assertNotNull(res);

        assertEquals(postDto.getTitle(),res.getTitle());
        assertEquals(Set.of("tag1","tag2"),res.getTags());

        verify(postRepository).findById(postId);
        verify(postRepository).save(post);
        verify(modelMapper).map(post,PostDto.class);
    }

    @Test
    void testUpdatePostNotFound(){
        String postId = "0";
        NewPostDto newPostDto = new NewPostDto();

        when(postRepository.findById(postId)).thenReturn(Optional.empty());
        assertThrows(PostNotFoundException.class,() -> postService.updatePost(postId,newPostDto));

        verify(postRepository).findById(postId);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void removePost(){
        String postId = "1000";
        PostDto postDto = new PostDto("1000", "title","content","user", LocalDateTime.now(),Set.of("tag1"),0,null);
        Post post = new Post("title","content","author",Set.of("tag1"));

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(modelMapper.map(post,PostDto.class)).thenReturn(postDto);

        PostDto res = postService.removePost(postId);
        assertNotNull(res);

        assertEquals(postDto.getTitle(),res.getTitle());

        verify(postRepository).findById(postId);
        verify(postRepository).deleteById(postId);
        verify(modelMapper).map(post, PostDto.class);
    }

    @Test
    void removePostNotFound(){
        String postId = "0";

        when(postRepository.findById(postId)).thenReturn(Optional.empty());
        assertThrows(PostNotFoundException.class,() -> postService.removePost(postId));

        verify(postRepository).findById(postId);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void addComment(){
        String postId = "1000";
        String author = "author";
        NewCommentDto newCommentDto = new NewCommentDto();
        CommentDto commentDto = new CommentDto(author,newCommentDto.getMessage(),LocalDateTime.now(),0);
        PostDto postDto = new PostDto("1000", "title","content","user", LocalDateTime.now(),Set.of("tag1"),0,List.of(commentDto));
        Post post = new Post("title","content","author",Set.of("tag1"));

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);
        when(modelMapper.map(post,PostDto.class)).thenReturn(postDto);

        PostDto res = postService.addComment(postId,author,newCommentDto);
        assertNotNull(res);

        assertEquals(1, res.getComments().size());
        assertEquals(author, res.getComments().getFirst().getUser());

        verify(postRepository).findById(postId);
        verify(postRepository).save(post);
        verify(modelMapper).map(post,PostDto.class);
    }

    @Test
    void addCommentNotFound(){
        String postId = "0";

        when(postRepository.findById(postId)).thenReturn(Optional.empty());
        assertThrows(PostNotFoundException.class,()  -> postService.addComment(postId,"author",new NewCommentDto()));

        verify(postRepository).findById(postId);
        verifyNoMoreInteractions(postRepository);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testFindPostsByAuthor() {
        String author = "author";
        List<Post> posts = List.of(
                new Post("title", "content", "author", Set.of("tag1")),
                new Post("title1", "content1", "author", Set.of("tag2"))
        );
        PostDto postDto1 = new PostDto("1000", "title", "content", "author", LocalDateTime.now(), Set.of("tag1"), 0, null);
        PostDto postDto2 = new PostDto("2000", "title1", "content1", "author", LocalDateTime.now(), Set.of("tag2"), 0, null);


        when(postRepository.findPostsByAuthorIgnoreCase(author)).thenReturn(posts.stream());
        when(modelMapper.map(any(Post.class), eq(PostDto.class)))
                .thenReturn(postDto1, postDto2);

        List<PostDto> res = postService.findPostsByAuthor(author);
        assertNotNull(res);
        List<String> resultIds = res.stream().map(PostDto::getId).toList();

        assertEquals(2, res.size());
        assertThat(resultIds).containsExactlyInAnyOrder("1000", "2000");

        verify(postRepository).findPostsByAuthorIgnoreCase(author);
        verify(modelMapper, times(2)).map(any(Post.class), eq(PostDto.class));
    }

    @Test
    void testFindPostsByAuthorEmpty(){
        String author = "author";

        when(postRepository.findPostsByAuthorIgnoreCase(author)).thenReturn(Stream.empty());

        List<PostDto> result = postService.findPostsByAuthor(author);

        assertTrue(result.isEmpty());

        verify(postRepository).findPostsByAuthorIgnoreCase(author);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testFindPostsByTags() {
        Set<String> tags = Set.of("tag1","tag2");
        List<Post> posts = List.of(
                new Post("title", "content", "author", Set.of("tag1")),
                new Post("title1", "content1", "author", Set.of("tag2"))
        );
        PostDto postDto1 = new PostDto("1000", "title", "content", "author", LocalDateTime.now(), Set.of("tag1"), 0, null);
        PostDto postDto2 = new PostDto("2000", "title1", "content1", "author1", LocalDateTime.now(), Set.of("tag2"), 0, null);


        when(postRepository.findPostsByTagsIgnoreCaseIn(tags)).thenReturn(posts.stream());
        when(modelMapper.map(any(Post.class), eq(PostDto.class)))
                .thenReturn(postDto1, postDto2);

        List<PostDto> res = postService.findPostsByTags(tags);
        assertNotNull(res);
        List<String> resultIds = res.stream().map(PostDto::getId).toList();

        assertEquals(2, res.size());
        assertThat(resultIds).containsExactlyInAnyOrder("1000", "2000");

        verify(postRepository).findPostsByTagsIgnoreCaseIn(tags);
        verify(modelMapper, times(2)).map(any(Post.class), eq(PostDto.class));
    }

    @Test
    void testFindPostsByPeriod() {
        LocalDateTime from = LocalDateTime.now().minusDays(1);
        LocalDateTime to = LocalDateTime.now().plusDays(1);
        PeriodDto periodDto = new PeriodDto(from,to);
        List<Post> posts = List.of(
                new Post("title", "content", "author", Set.of("tag1")),
                new Post("title1", "content1", "author", Set.of("tag2"))
        );
        PostDto postDto1 = new PostDto("1000", "title", "content", "author", LocalDateTime.now(), Set.of("tag1"), 0, null);
        PostDto postDto2 = new PostDto("2000", "title1", "content1", "author1", LocalDateTime.now(), Set.of("tag2"), 0, null);


        when(postRepository.findPostsByDateCreatedBetween(periodDto.getDateFrom(),periodDto.getDateTo())).thenReturn(posts.stream());
        when(modelMapper.map(any(Post.class), eq(PostDto.class)))
                .thenReturn(postDto1, postDto2);

        List<PostDto> res = postService.findPostsByPeriod(periodDto);
        assertNotNull(res);
        List<String> resultIds = res.stream().map(PostDto::getId).toList();

        assertEquals(2, res.size());
        assertThat(resultIds).containsExactlyInAnyOrder("1000", "2000");

        verify(postRepository).findPostsByDateCreatedBetween(periodDto.getDateFrom(),periodDto.getDateTo());
        verify(modelMapper, times(2)).map(any(Post.class), eq(PostDto.class));
    }

    @Test
    void testAddLike() {
        String postId = "1000";
        Post post = new Post("title", "content", "user", new HashSet<>(Set.of("tag1", "tag2")));

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        postService.addLike(postId);
        assertEquals(1, post.getLikes());

        verify(postRepository).findById(postId);
        verify(postRepository).save(post);
    }

    @Test
    void testAddLikeNotExist() {
        String postId = "0";

        when(postRepository.findById(postId)).thenReturn(Optional.empty());
        assertThrows(PostNotFoundException.class, () -> postService.addLike(postId));

        verify(postRepository).findById(postId);
        verifyNoMoreInteractions(postRepository);
    }

}
