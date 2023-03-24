package ch.hftm.blog.dto;

import ch.hftm.blog.entity.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserDto {

    private String name;

    public static UserDto fromUser (User user) {
        return UserDto.builder().name(user.getName()).build();
    }
}
