package bd.edu.diu.cis.classroom.repository;

import bd.edu.diu.cis.classroom.model.Classroom;
import org.springframework.data.repository.CrudRepository;

public interface ClassroomRepository extends CrudRepository<Classroom, Long> {
    Classroom findClassroomByUrl(String url);
}
