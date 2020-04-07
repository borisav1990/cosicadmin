package registry.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import registry.models.Car;

public interface CarRepository extends JpaRepository<Car, Long> {

}
