package emma_media.video.repository;

import emma_media.video.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    List<Video> findByUserId(Long userId);

    @Query("SELECT v FROM Video v WHERE LOWER(v.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(v.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Video> searchVideos(@Param("keyword") String keyword);
}
