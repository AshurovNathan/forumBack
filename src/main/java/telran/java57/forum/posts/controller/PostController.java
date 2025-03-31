package telran.java57.forum.posts.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import telran.java57.dto.PeriodDto;
import telran.java57.forum.posts.dto.CommentDto;
import telran.java57.forum.posts.dto.NewPostDto;
import telran.java57.forum.posts.dto.PostDto;
import telran.java57.forum.posts.service.PostService;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/forum")
public class PostController {

    final PostService postService;

    @PostMapping("/post/{author}")
    public PostDto addNewPost(@PathVariable String author, @RequestBody NewPostDto newPostDto){
        return postService.addNewPost(author,newPostDto);
    }

    @GetMapping("/post/{postId}")
    public PostDto findPostById(@PathVariable String postId){
        return postService.findPostById(postId);
    }

    @PutMapping("/post/{postId}")
    public PostDto updatePost(@PathVariable String postId, @RequestBody NewPostDto newPostDto){
        return postService.updatePost(postId,newPostDto);
    }

    @DeleteMapping("/post/{postId}")
    public PostDto removePost(@PathVariable String postId){
        return postService.removePost(postId);
    }

    @GetMapping("/posts/author/{user}")
    public List<PostDto> findPostsByAuthor(@PathVariable String user){
        return postService.findPostsByAuthor(user);
    }

    @PutMapping("/post/{postId}/comment/{user}")
    public PostDto addComment(@PathVariable String postId, @PathVariable String user, @RequestBody CommentDto commentDto){
        return postService.addComment(postId,user,commentDto);
    }

    @PostMapping("/posts/tags")
    public List<PostDto> findPostsByTags(@RequestBody Set<String> tags){
        return postService.findPostsByTags(tags);
    }

    @PostMapping("/posts/period")
    public List<PostDto> findPostsByPeriod(@RequestBody PeriodDto periodDto){
        return postService.findPostsByPeriod(periodDto);
    }

    @PutMapping("/post/{postId}/like")
    public Integer addLike(@PathVariable String postId) {
        return postService.addLike(postId);
    }
}
