package bd.edu.diu.cis.classroom.repository;

import bd.edu.diu.cis.classroom.model.SectionUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionUserRepository extends CrudRepository<SectionUser, Long> {
    List<SectionUser> findSectionUsersBySectionClassroomUrlOrderByRegisteredDesc(String url);
    SectionUser findSectionUsersByStudentUsername(String name);
}
