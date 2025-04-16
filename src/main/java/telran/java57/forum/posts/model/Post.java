package telran.java57.forum.posts.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Document(collection = "posts")
public class Post {
    String id;
    @Setter
    String title;
    @Setter
    String content;
    @Setter
    String author;
    LocalDateTime dateCreated = LocalDateTime.now();
    Set<String> tags = new HashSet<String>();
    Integer likes = 0;
    List<Comment> comments = new ArrayList<>();

    public Post(String title, String content, String author, Set<String> tags) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.tags = tags;
    }

    public <E> Post(String number, String title, String content, String user, LocalDateTime now, Set<E> tag1, int i, Object o) {
    }

    public boolean addComment(Comment comment){
        return comments.add(comment);
    }

    public boolean removeComment(Comment comment){
        return comments.remove(comment);
    }

    public boolean addTag(String tag){
        return tags.add(tag);
    }

    public boolean removeTag(String tag){
        return tags.remove(tag);
    }

    public void addLike(){
        likes++;
    }
}
