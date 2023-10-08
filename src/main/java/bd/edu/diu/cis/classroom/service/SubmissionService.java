package bd.edu.diu.cis.classroom.service;

import bd.edu.diu.cis.classroom.model.Submission;
import bd.edu.diu.cis.classroom.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubmissionService {
    @Autowired
    private SubmissionRepository submissionRepository;

    public Submission getById(long id) {
        return submissionRepository.getSubmissionById(id);
    }

    public List<Submission> getSubmissionListByPostId(long id) {
        return submissionRepository.getSubmissionsByPostId(id);
    }

    public void save(Submission submission) {
        submissionRepository.save(submission);
    }

    public Submission getByPostIdAndUser(long id, String email) {
        return submissionRepository.getSubmissionByPostIdAndStudentUsername(id, email);
    }
}
