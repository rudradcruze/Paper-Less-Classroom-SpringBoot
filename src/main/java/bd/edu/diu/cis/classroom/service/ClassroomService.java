package bd.edu.diu.cis.classroom.service;

import bd.edu.diu.cis.classroom.model.Classroom;
import bd.edu.diu.cis.classroom.repository.ClassroomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassroomService {

    @Autowired
    private ClassroomRepository classRoomRepository;

    public List<Classroom> listAll() {
        return (List<Classroom>) classRoomRepository.findAll();
    }

    public Classroom findByUrl(String url) {
        return classRoomRepository.findClassroomByUrl(url);
    }
    public Classroom findByInviteCode(String code) {return classRoomRepository.findClassroomByInviteCode(code);}

    public void save(Classroom classroom) {
        classRoomRepository.save(classroom);
    }
}
