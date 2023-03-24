package ch.hftm.blog.resource;

import ch.hftm.blog.dto.EntryDto;
import ch.hftm.blog.entity.Comment;
import ch.hftm.blog.entity.Entry;
import ch.hftm.blog.entity.User;
import ch.hftm.blog.entity.UserRole;
import org.eclipse.microprofile.graphql.*;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@GraphQLApi
public class BlogResource extends AResource {

    @Query("entries")
    @Description("get all blog entries")
    public List<EntryDto> entries (Integer from, Integer to, String search) {
        List<Entry> entryList;
        if (search == null || search.isEmpty()) {
            entryList = Entry.listAll();
        } else {
            entryList = Entry.list("title LIKE ?1 or content LIKE ?1", "%" + search + "%");
        }

        if (from != null && to != null) {
            entryList = entryList.subList(from, to);
        } else if (from != null) {
            entryList = entryList.subList(from, entryList.size());
        } else if (to != null) {
            entryList = entryList.subList(0, to);
        }

        return entryList.stream().map(e -> EntryDto.fromEntry(e)).collect(Collectors.toList());
    }

    @Mutation("addEntry")
    @Description("creates a new blog entry")
    @Transactional
    public EntryDto addEntry (@NotNull @Size(min = 3, max = 100) String title,
                              @NotNull @Size(min = 3, max = 5000) String content) throws GraphQLException {

        User user = mustUser();

        Entry entry = Entry.builder()
                .title(title)
                .content(content)
                .approved(false)
                .autor(user)
                .comments(new ArrayList<>())
                .likes(new HashSet<>())
                .build();

        entry.persist();

        return EntryDto.fromEntry(entry);
    }

    @Mutation("addComment")
    @Description("creates a new blog comment")
    @Transactional
    public EntryDto addComment (long id, @NotNull @Size(min = 1, max = 500) String comment) throws GraphQLException {

        User user = mustUser();
        Entry entry = mustEntry(id);

        Comment commentEntity = Comment.builder()
                .comment(comment)
                .user(user)
                .build();

        commentEntity.persist();

        entry.getComments().add(commentEntity);
        entry.persist();

        return EntryDto.fromEntry(entry);
    }

    @Mutation("likeEntry")
    @Description("like or unlike an blog entry")
    @Transactional
    public EntryDto likeEntry (long id, boolean like) throws GraphQLException {

        User user = mustUser();
        Entry entry = mustEntry(id);

        if (like) {
            entry.getLikes().add(user);
        } else {
            entry.getLikes().remove(user);
        }

        return EntryDto.fromEntry(entry);
    }

    @Mutation("deleteEntry")
    @Description("delete a blog entry. Users can only delete their own Blogs. Admins can delete all.")
    @Transactional
    public boolean deleteEntry (long id) throws GraphQLException {
        User user = mustUser();
        Entry entry = mustEntry(id);
        if (entry.getAutor().getId().equals(user.getId()) || user.getRole().equals(UserRole.ADMIN)) {
            entry.delete();
            return true;
        } else {
            throw new GraphQLException("not allowed");
        }
    }

    public Entry mustEntry (long id) throws GraphQLException {
        Entry entry = Entry.findById(id);
        if (entry == null) {
            throw new GraphQLException("no entry for the given id");
        }
        return entry;
    }
}
