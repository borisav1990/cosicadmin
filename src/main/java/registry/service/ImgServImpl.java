package registry.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import registry.models.Image;
import registry.repository.ImageRepository;

@Service
@Transactional
public class ImgServImpl implements ImageService {

	@Autowired
	private ImageRepository imageRepository;

	@Override
	public List<Image> getImageByCar(Long vehicleId) {
		return imageRepository.findAllById(vehicleId);

	}

}
