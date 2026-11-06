package sw2.lab5.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import sw2.lab5.Entity.Post;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query("select p from Post p where p.user.id_user = :idUser")
    List<Post> findByUser_Id_user(@Param("idUser") int idUser);
}
