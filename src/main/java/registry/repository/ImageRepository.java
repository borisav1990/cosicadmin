package registry.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import registry.models.Image;

@Repository
public interface ImageRepository extends CrudRepository<Image, Long> {

	@Query("select f from Image as f where f.car.id = ?1")
	List<Image> findAllById(Long vehicleId);

	@Modifying
	@Query("delete from Image as f where f.car.id = ?1 and f.modifiedFileName in (?2)")
	void deleteImagesFromDB(Long id, String[] arrayForDelete);

	@Modifying
	@Query("delete from Image as f where f.car.id = ?1")
	void deleteAllImagesByVehicle(Long vehicleId);

	// @Query("select f from NewsImage as f where f.news.id = ?1")
	// List<NewsImage> findNewsImageById(Long newsId);

	// @Modifying
	// @Query("delete from NewsImage as f where f.news.id = ?1")
	// void deleteImagesByNewsId(Long userId);

	// @Modifying
	// @Query("delete from NewsImage as f where f.news.id = ?1 and
	// f.modifiedFileName in (?2)")
	// void deleteFilesByNewsIdAndImageNames(Long id, String[] arrayForDelete);

}
