package registry.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import registry.models.Owner;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

}
