package bd.edu.diu.cis.classroom.repository;

import bd.edu.diu.cis.classroom.model.ClassroomUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassroomUserRepository extends CrudRepository<ClassroomUser, Long> {
    List<ClassroomUser> findClassroomUsersByClassroomUrl(String url);
}
