package telran.java57.forum.posts.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NewPostDto {
    String title;
    String content;
    Set<String> tags;
}
