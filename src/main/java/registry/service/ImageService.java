package registry.service;

import java.util.List;

import registry.models.Image;

public interface ImageService {

	List<Image> getImageByCar(Long vehicleId);

}
