package telran.java57.forum.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PeriodDto {
    LocalDateTime dateFrom;
    LocalDateTime dateTo;
}
