package registry.service;

import java.util.List;

import registry.models.ServiceBook;

public interface BookService {

	List<ServiceBook> getAllServiceFromCar(Long vehicleId);

	List<ServiceBook> getAllService();

	boolean saveServiceToBook(ServiceBook serviceBook);

	ServiceBook getOneService(Long serviceId);

	boolean deleteService(Long serviceId);

}
