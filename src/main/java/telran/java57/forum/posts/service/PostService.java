package telran.java57.forum.posts.service;


import telran.java57.forum.posts.dto.PeriodDto;
import telran.java57.forum.posts.dto.NewCommentDto;
import telran.java57.forum.posts.dto.NewPostDto;
import telran.java57.forum.posts.dto.PostDto;

import java.util.List;
import java.util.Set;


public interface PostService {

    PostDto addNewPost(String author, NewPostDto newPostDto);

    PostDto findPostById(String postId);

    PostDto updatePost(String postId,NewPostDto newPostDto);

    PostDto removePost(String postId);

    List<PostDto> findPostsByAuthor(String user);

    PostDto addComment(String postId, String user, NewCommentDto newCommentDto);

    List<PostDto> findPostsByTags(Set<String> tags);

    List<PostDto> findPostsByPeriod(PeriodDto periodDto);

    void addLike(String postId);
}
