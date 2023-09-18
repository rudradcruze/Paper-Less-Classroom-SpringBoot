package bd.edu.diu.cis.classroom.repository;

import bd.edu.diu.cis.classroom.model.ClassroomTeacher;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassroomTeacherRepository extends CrudRepository<ClassroomTeacher, Long> {
    List<ClassroomTeacher> findClassroomTeachersByClassroomUrlOrderByRegisteredDesc(String url);
    ClassroomTeacher findClassroomTeacherByClassroomUrlAndTeacherUsername(String url, String email);
    List<ClassroomTeacher> findClassroomTeachersByTeacherUsername(String email);
}
