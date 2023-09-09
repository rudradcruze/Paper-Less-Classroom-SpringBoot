package bd.edu.diu.cis.classroom.service;

import bd.edu.diu.cis.classroom.model.Post;
import bd.edu.diu.cis.classroom.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public Post getById(long id) {
        return postRepository.findPostById(id);
    }

    public List<Post> getAllByClassroomUrlDateDesc(String url) {
        return postRepository.findPostsByClassroomUrlOrderByPostDateDesc(url);
    }

    public void save(Post post) {
        postRepository.save(post);
    }
}
