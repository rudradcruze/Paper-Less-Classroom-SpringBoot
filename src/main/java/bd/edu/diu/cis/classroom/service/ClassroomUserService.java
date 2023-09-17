package bd.edu.diu.cis.classroom.service;

import bd.edu.diu.cis.classroom.model.ClassroomTeacher;
import bd.edu.diu.cis.classroom.repository.ClassroomUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassroomUserService {

    @Autowired
    private ClassroomUserRepository classroomUserRepository;
    public void save(ClassroomTeacher classroomTeacher) {classroomUserRepository.save(classroomTeacher);}

    public List<ClassroomTeacher> listUsersByClassroomUrl(String url) {
        return (List<ClassroomTeacher>) classroomUserRepository.findClassroomUsersByClassroomUrlOrderByRegisteredDesc(url);
    }

}
