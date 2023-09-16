package bd.edu.diu.cis.classroom.repository;

import bd.edu.diu.cis.classroom.model.Section;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends CrudRepository<Section, Long> {
    List<Section> findSectionsByClassroomUrl(String url);
    Section findSectionById(long id);
    Section findSectionByName(String name);
    Section findSectionByJoinCode(String code);
}
