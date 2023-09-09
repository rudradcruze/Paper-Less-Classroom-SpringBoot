package bd.edu.diu.cis.classroom.service;

import bd.edu.diu.cis.classroom.model.ClassroomUser;
import bd.edu.diu.cis.classroom.repository.ClassroomUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassroomUserService {

    @Autowired
    private ClassroomUserRepository classroomUserRepository;

    public void save(ClassroomUser classroomUser) {classroomUserRepository.save(classroomUser);}

    public List<ClassroomUser> listUsersByClassroomUrl(String url) {
        return (List<ClassroomUser>) classroomUserRepository.findClassroomUsersByClassroomUrlOrderByRegisteredDesc(url);
    }

}
