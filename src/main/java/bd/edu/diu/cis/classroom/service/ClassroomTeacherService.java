package bd.edu.diu.cis.classroom.service;

import bd.edu.diu.cis.classroom.model.ClassroomTeacher;
import bd.edu.diu.cis.classroom.repository.ClassroomTeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassroomTeacherService {

    @Autowired
    private ClassroomTeacherRepository classroomUserRepository;
    public void save(ClassroomTeacher classroomTeacher) {classroomUserRepository.save(classroomTeacher);}

    public List<ClassroomTeacher> listTeachersByClassroomUrl(String url) {
        return (List<ClassroomTeacher>) classroomUserRepository.findClassroomTeachersByClassroomUrlOrderByRegisteredDesc(url);
    }

    public ClassroomTeacher getByTeacherUserNameAndClassroomUrl(String url, String name) {
        return classroomUserRepository.findClassroomTeacherByClassroomUrlAndTeacherUsername(url, name);
    }

    public List<ClassroomTeacher> getAllTeacherRequestByTeacherUserName(String email) {
        return classroomUserRepository.findClassroomTeachersByTeacherUsernameOrderByRegisteredDesc(email);
    }

}