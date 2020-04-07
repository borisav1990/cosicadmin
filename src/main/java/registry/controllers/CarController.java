package registry.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import registry.models.Car;
import registry.models.Image;
import registry.models.Owner;
import registry.models.ServiceBook;
import registry.service.BookService;
import registry.service.CarService;
import registry.service.ImageService;

@Controller
@RequestMapping("/admin")
public class CarController {

	@Autowired
	private CarService carService;

	@Autowired
	private ImageService imgService;

	@Autowired
	private BookService bookService;

	@Autowired
	private ServletContext context;

	// -----------------------VEHICLE PART-------------------------------------

	@RequestMapping("/home")
	public String adminView(Model model) throws ParseException {
		List<Car> listOfVehicle = carService.getAllVehicles();
		model.addAttribute("listOfVehicle", listOfVehicle);
		return "pages/home";
	}

	@RequestMapping("/addVehicle")
	public String addVehicle(Model model) {
		List<Owner> listOfOwner = carService.getAllOwners();
		List<Car> listOfVehicle = carService.getAllVehicles();
		model.addAttribute("listOfVehicle", listOfVehicle);
		model.addAttribute("listOfOwner", listOfOwner);
		model.addAttribute("objOfVehicle", new Car());
		return "pages/add_vehicle";
	}

	@RequestMapping("/editVehicle/{vehicleId}")
	public String editVehicle(Model model, @PathVariable Long vehicleId) {
		List<Owner> listOfOwner = carService.getAllOwners();
		List<Car> listOfVehicle = carService.getAllVehicles();
		List<Image> listOfImg = imgService.getImageByCar(vehicleId);
		Car objCar = carService.getCar(vehicleId);
		boolean isUpdated = true;
		model.addAttribute("isUpdated", isUpdated);
		model.addAttribute("listOfImg", listOfImg);
		model.addAttribute("listOfVehicle", listOfVehicle);
		model.addAttribute("listOfOwner", listOfOwner);
		model.addAttribute("objOfVehicle", objCar);
		return "pages/add_vehicle";
	}

	@RequestMapping("/saveVehicle")
	public String saveVehicle(@ModelAttribute Car car, RedirectAttributes redirectAttr) {
		String saved = carService.saveVehicle(car);
		if (saved.equals("success")) {
			redirectAttr.addFlashAttribute("saveSuccess", "Uspešno snimljeno!");
			return "redirect:/admin/addVehicle";
		} else {
			redirectAttr.addFlashAttribute("error", saved);
			return "redirect:/admin/addVehicle";
		}
	}

	@GetMapping("/details/{vehicleId}")
	public String details(Model model, @PathVariable Long vehicleId) {
		Car objCar = carService.getCar(vehicleId);
		List<Image> listOfImg = imgService.getImageByCar(vehicleId);
		List<ServiceBook> listOfSer = bookService.getAllServiceFromCar(vehicleId);
		double summePrice = 0;
		for (ServiceBook serviceBook : listOfSer) {
			summePrice = summePrice + serviceBook.getPrice();
		}
		model.addAttribute("summePrice", summePrice);
		model.addAttribute("listOfSer", listOfSer);
		model.addAttribute("listOfImg", listOfImg);
		model.addAttribute("vehicle", objCar);
		return "pages/details_vehicle";
	}

	@GetMapping("/deleteVehicle/{vehicleId}")
	public String deleteVehicle(Model model, @PathVariable Long vehicleId, RedirectAttributes redirectAttr) {
		boolean succcessDelete = carService.deleteVehicleById(vehicleId);
		if (succcessDelete) {
			redirectAttr.addFlashAttribute("deleteSuccess", "Vozilo uspešno obrisano!");
			return "redirect:/admin/addVehicle";
		} else {
			redirectAttr.addFlashAttribute("error", "Vozilo nije obrisano!");
			return "redirect:/admin/addVehicle";
		}
	}

	// ------------------------OWNER PART------------------------------------------

	@RequestMapping("/addOwner")
	public String addOwner(Model model) {
		model.addAttribute("objOfOwner", new Owner());
		List<Owner> listOfOwner = carService.getAllOwners();
		model.addAttribute("listOfOwner", listOfOwner);
		model.addAttribute("isUpdate", false);
		return "pages/add_owner";
	}

	@RequestMapping("/saveOwner")
	public String saveOwner(Model model, @ModelAttribute Owner owner, RedirectAttributes redirectAttr) {
		boolean savedSuccess = carService.saveOwner(owner);

		if (savedSuccess) {
			redirectAttr.addFlashAttribute("saveSuccess", "Uspešno snimljeno!");
			return "redirect:/admin/addOwner";
		} else {
			redirectAttr.addFlashAttribute("error", "Prekinuto ! ! ! Proverite polja možda su ostala prazna.");
			return "redirect:/admin/addOwner";
		}

	}

	@GetMapping("/deleteOwner/{ownerId}")
	public String deleteOwner(Model model, @PathVariable Long ownerId, RedirectAttributes redirectAttr) {
		boolean succcessDelete = carService.deleteOwnerById(ownerId);
		if (succcessDelete) {
			redirectAttr.addFlashAttribute("deleteSuccess", "Uspešno obrisano!");
			return "redirect:/admin/addOwner";
		} else {
			redirectAttr.addFlashAttribute("error", "Nije obrisano!");
			return "redirect:/admin/addOwner";
		}
	}

	@GetMapping("/editOwner/{ownerId}")
	public String editUser(Model model, @PathVariable Long ownerId) {
		Owner objOfOwner = carService.getOneOfOwner(ownerId);
		model.addAttribute("objOfOwner", objOfOwner);
		List<Owner> listOfOwner = carService.getAllOwners();
		model.addAttribute("listOfOwner", listOfOwner);
		model.addAttribute("isUpdate", true);

		return "pages/add_owner";
	}

	// -------------------BOOKSERVICE PART--------------------------------

	@RequestMapping("/addService")
	public String addService(Model model) {
		List<ServiceBook> listOfServ = bookService.getAllService();
		model.addAttribute("listOfServ", listOfServ);
		List<Car> listOfVehicle = carService.getAllVehicles();
		model.addAttribute("listOfVehicle", listOfVehicle);
		model.addAttribute("objOfBook", new ServiceBook());
		return "pages/add_service";
	}

	@RequestMapping("/saveService")
	public String saveService(Model model, @ModelAttribute ServiceBook serviceBook, RedirectAttributes redirectAttr) {

		boolean savedSuccess = bookService.saveServiceToBook(serviceBook);

		if (savedSuccess) {
			redirectAttr.addFlashAttribute("saveSuccess", "Servis uspešno snimljen!");
			return "redirect:/admin/addService";
		} else {
			redirectAttr.addFlashAttribute("error", "Prekinuto ! ! ! Proverite polja možda su ostala prazna.");
			return "redirect:/admin/addService";
		}

	}

	@GetMapping("/editService/{serviceId}")
	public String editService(Model model, @PathVariable Long serviceId) {
		List<ServiceBook> listOfServ = bookService.getAllService();
		model.addAttribute("listOfServ", listOfServ);
		List<Car> listOfVehicle = carService.getAllVehicles();
		model.addAttribute("listOfVehicle", listOfVehicle);
		ServiceBook objOfBook = bookService.getOneService(serviceId);
		model.addAttribute("objOfBook", objOfBook);
		model.addAttribute("isUpdate", true);

		return "pages/add_service";
	}

	@GetMapping("/deleteService/{serviceId}")
	public String deleteService(Model model, @PathVariable Long serviceId, RedirectAttributes redirectAttr) {
		boolean succcessDelete = bookService.deleteService(serviceId);
		if (succcessDelete) {
			redirectAttr.addFlashAttribute("deleteSuccess", "Uspešno obrisano!");
			return "redirect:/admin/addService";
		} else {
			redirectAttr.addFlashAttribute("error", "Nije obrisano!");
			return "redirect:/admin/addService";
		}
	}

	// ------------------------DOWNLOAD PDF----------------------

	@GetMapping("/createdPdf/{vehicleId}")
	public void createdPdf(@PathVariable Long vehicleId, HttpServletRequest request, HttpServletResponse response) {
		Car car = carService.getCar(vehicleId);
		List<ServiceBook> service = bookService.getAllServiceFromCar(vehicleId);

		boolean isFlag = carService.createdPdf(car, service, context, request, response);
		System.out.println("--------" + isFlag);
		if (isFlag) {
			String fullPath = request.getServletContext()
					.getRealPath("/resources/reports/" + "Vozilo-administrator" + ".pdf");
			filedownload(fullPath, response, "Vozilo-administrator.pdf");
		}
	}

	private void filedownload(String fullPath, HttpServletResponse response, String fileName) {
		File file = new File(fullPath);
		final int BUFFER_SIZE = 4096;
		if (file.exists()) {
			try {
				FileInputStream inputStream = new FileInputStream(file);
				String mimeType = context.getMimeType(fullPath);
				response.setContentType(mimeType);
				response.setHeader("content-disposition", "attachment; filename=" + fileName);
				OutputStream outputStream = response.getOutputStream();
				byte[] buffer = new byte[BUFFER_SIZE];
				int bytesRead = -1;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
				inputStream.close();
				outputStream.close();
				file.delete();

			} catch (Exception e) {
				e.printStackTrace();

			}

		}

	}

}
