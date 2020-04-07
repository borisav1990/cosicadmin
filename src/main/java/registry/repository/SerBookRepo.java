package registry.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import registry.models.ServiceBook;

public interface SerBookRepo extends JpaRepository<ServiceBook, Long> {

	@Query("select f from ServiceBook as f where f.car.id = ?1")
	List<ServiceBook> getAllServiceFromCar(Long vehicleId);

}
