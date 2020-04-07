package registry.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import registry.models.ServiceBook;
import registry.repository.SerBookRepo;

@Service
@Transactional
public class BookSerImpl implements BookService {

	@Autowired
	private SerBookRepo bookRepository;

	@Override
	public List<ServiceBook> getAllServiceFromCar(Long vehicleId) {
		return bookRepository.getAllServiceFromCar(vehicleId);

	}

	@Override
	public List<ServiceBook> getAllService() {
		return bookRepository.findAll();
	}

	@Override
	public boolean saveServiceToBook(ServiceBook serviceBook) {
		if (serviceBook.getCar() != null && !serviceBook.getWorkshop().equals("")
				&& !serviceBook.getWork().equals("")) {
			bookRepository.save(serviceBook);
			return true;
		} else {
			return false;

		}

	}

	@Override
	public ServiceBook getOneService(Long serviceId) {
		return bookRepository.findById(serviceId).get();

	}

	@Override
	public boolean deleteService(Long serviceId) {
		bookRepository.deleteById(serviceId);
		return true;
	}

}
