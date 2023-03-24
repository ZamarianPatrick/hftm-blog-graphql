package ch.hftm.blog.dto;

import ch.hftm.blog.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class CommentDto {

    private long id;
    private String userName;
    private String comment;
    private LocalDateTime date;

    public static CommentDto fromComment(Comment comment) {
        CommentDto dto = CommentDto.builder()
                .id(comment.getId())
                .userName(comment.getUser().getName())
                .comment(comment.getComment())
                .date(comment.getDate())
                .build();
        return dto;
    }
}
