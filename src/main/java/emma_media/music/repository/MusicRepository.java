package emma_media.music.repository;

import emma_media.music.entity.Music;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicRepository extends JpaRepository<Music, Long> {

    List<Music> findByUserId(Long userId);

    @Query("SELECT m FROM Music m WHERE LOWER(m.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(m.artist) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Music> searchMusic(@Param("keyword") String keyword);
}