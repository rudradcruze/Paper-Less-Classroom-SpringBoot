package bd.edu.diu.cis.classroom.repository;

import bd.edu.diu.cis.classroom.model.Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {
    Post findPostById(long id);
    List<Post> findPostsByClassroomUrl(String url);
}
