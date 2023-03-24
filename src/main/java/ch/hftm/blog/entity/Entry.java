package ch.hftm.blog.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.smallrye.common.constraint.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@NoArgsConstructor @AllArgsConstructor
@Builder
@Getter
@Entity
public class Entry extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean approved;
    
    @Size(max= 200)
    private String title;

    @Size(max= 10000)
    @Column(length = 10000)
    private String content;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "entry_id")
    private List<Comment> comments;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author")
    private User autor;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<User> likes;

}