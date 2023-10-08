package bd.edu.diu.cis.classroom.repository;

import bd.edu.diu.cis.classroom.model.Submission;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends CrudRepository<Submission, Long> {
    Submission getSubmissionById(long id);
    List<Submission> getSubmissionsByPostId(long id);
    Submission getSubmissionByPostIdAndStudentUsername(long id, String userName);
}
