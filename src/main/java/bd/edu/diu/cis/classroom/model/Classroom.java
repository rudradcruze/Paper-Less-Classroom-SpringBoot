package bd.edu.diu.cis.classroom.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(length = 1000)
    private String description;
    private String section;
    private String room;
    private String subject;
    private String url;
    private boolean activate;
    private String inviteCode;
    private String imageName;
    private boolean inviteCodeActivate;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @OneToMany(mappedBy = "classroom", fetch = FetchType.LAZY)
    private List<ClassroomUser> students = new ArrayList<>();

    @OneToMany(mappedBy = "classroom", fetch = FetchType.LAZY)
    private List<Post> posts = new ArrayList<>();
}
