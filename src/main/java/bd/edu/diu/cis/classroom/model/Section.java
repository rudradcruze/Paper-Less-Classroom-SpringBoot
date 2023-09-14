package bd.edu.diu.cis.classroom.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    private String name;
    private String meetingLink;
    private String joinCode;
    private boolean status;
    private boolean meetingLinkStatus;
}
