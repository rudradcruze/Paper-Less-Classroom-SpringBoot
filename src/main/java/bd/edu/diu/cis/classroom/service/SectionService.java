package bd.edu.diu.cis.classroom.service;

import bd.edu.diu.cis.classroom.model.Section;
import bd.edu.diu.cis.classroom.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectionService {

    @Autowired
    private SectionRepository sectionRepository;

    public List<Section> listSectionByClassroomUrl(String url) {
        return (List<Section>) sectionRepository.findSectionsByClassroomUrl(url);
    }

    public Section getById(long id) {
        return sectionRepository.findSectionById(id);
    }

    public Section getByName(String name) {return sectionRepository.findSectionByName(name);}

    public void save(Section section) {
        sectionRepository.save(section);
    }

    public void delete(long id) {
        sectionRepository.delete(getById(id));
    }

}
