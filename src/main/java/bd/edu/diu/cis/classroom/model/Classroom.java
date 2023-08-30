package bd.edu.diu.cis.classroom.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Classroom {
    private Long id;
    private String name;
    private String description;
    private String section;
    private String room;
    private String subject;
    private boolean activate;
    private String inviteCode;
    private boolean inviteCodeActivate;
    private User teacher;
    private List<User> students;
}
