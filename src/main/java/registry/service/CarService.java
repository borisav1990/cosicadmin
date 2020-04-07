package registry.service;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import registry.models.Car;
import registry.models.Owner;
import registry.models.ServiceBook;

public interface CarService {

	String saveVehicle(Car car);

	List<Car> getAllVehicles();

	Car getCar(Long vehicleId);

	boolean deleteVehicleById(Long vehicleId);

	boolean saveOwner(Owner owner);

	List<Owner> getAllOwners();

	boolean deleteOwnerById(Long ownerId);

	Owner getOneOfOwner(Long ownerId);

	boolean createdPdf(Car car, List<ServiceBook> service, ServletContext context, HttpServletRequest request,
			HttpServletResponse response);

}
