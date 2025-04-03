package telran.java57.forum.posts.dto;

import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class PeriodDto {
    LocalDateTime dateFrom;
    LocalDateTime dateTo;
}
