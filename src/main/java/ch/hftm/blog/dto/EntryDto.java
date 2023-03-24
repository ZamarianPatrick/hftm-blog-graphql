package ch.hftm.blog.dto;

import ch.hftm.blog.entity.Entry;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@Getter
public class EntryDto {

    private long id;
    private boolean approved;
    private String title;
    private String content;
    private List<CommentDto> comments;
    private UserDto author;
    private int likesCount;
    private Set<UserDto> likes;

    public static EntryDto fromEntry(Entry entry) {
        EntryDto dto = EntryDto.builder()
                .id(entry.getId())
                .approved(entry.isApproved())
                .title(entry.getTitle())
                .content(entry.getContent())
                .author(UserDto.fromUser(entry.getAutor()))
                .comments(
                        entry.getComments()
                                .stream()
                                .map(comment -> CommentDto.fromComment(comment))
                                .collect(Collectors.toList()))
                .likesCount(entry.getLikes().size())
                .likes(
                        entry.getLikes()
                                .stream()
                                .map(user -> UserDto.fromUser(user))
                                .collect(Collectors.toSet()))
                .build();

        return dto;
    }

}
