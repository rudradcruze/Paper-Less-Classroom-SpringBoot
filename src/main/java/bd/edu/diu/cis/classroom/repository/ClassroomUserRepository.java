package bd.edu.diu.cis.classroom.repository;

import bd.edu.diu.cis.classroom.model.ClassroomTeacher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassroomUserRepository extends CrudRepository<ClassroomTeacher, Long> {
    List<ClassroomTeacher> findClassroomUsersByClassroomUrlOrderByRegisteredDesc(String url);
}
