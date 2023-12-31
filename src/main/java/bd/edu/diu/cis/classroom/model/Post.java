package bd.edu.diu.cis.classroom.model;

import lombok.*;

import javax.persistence.*;
import java.util.*;

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
    private boolean canSubmit;
    private String type;
    private String fileName;
    private int grade;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    private Date postDate;
    private Date dueDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "post_sections",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "section_id")
    )
    private List<Section> sections = new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<Submission> submissions = new ArrayList<>();
}
