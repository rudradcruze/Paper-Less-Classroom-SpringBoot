package bd.edu.diu.cis.classroom.service;

import bd.edu.diu.cis.classroom.model.SectionUser;
import bd.edu.diu.cis.classroom.repository.SectionUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectionUserService {
    @Autowired
    private SectionUserRepository sectionUserRepository;

    public void save(SectionUser sectionUser) {sectionUserRepository.save(sectionUser);}

    public List<SectionUser> listUsersByClassroomUrl(String url) {
        return (List<SectionUser>) sectionUserRepository.findSectionUsersBySectionClassroomUrlOrderByRegisteredDesc(url);
    }
}
