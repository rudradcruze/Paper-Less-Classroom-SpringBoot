package bd.edu.diu.cis.classroom.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200)
    private String title;

    @Column(length = 10000)
    private String content;
    private boolean status;
    private String type;
    private String fileName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    private Date postDate;
    private Date dueDate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "post_sections",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "section_id")
    )
    private Set<Section> sections = new HashSet<>();
}
