package registry.service;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import registry.models.Car;
import registry.models.Image;
import registry.models.Owner;
import registry.models.ServiceBook;
import registry.repository.CarRepository;
import registry.repository.ImageRepository;
import registry.repository.OwnerRepository;

@Service
@Transactional
public class CarServiceImp implements CarService {

	@Autowired
	private CarRepository carRepository;

	@Autowired
	private OwnerRepository ownerRepository;

	@Autowired
	private UploadPathService uploadService;

	@Autowired
	private ImageRepository ImgRep;

	@Autowired
	private ServletContext context;

	@Override
	public String saveVehicle(Car car) {
		CheckCar check = new CheckCar();
		boolean a = check.checkField(car);
		if (a) {

			// ----------------SAVE-------------------------------------
			String toUpper = car.getLicensePlate().toUpperCase();
			car.setLicensePlate(toUpper);
			carRepository.save(car);
			if (car != null && car.getImageList() != null && car.getImageList().size() > 0) {
				for (MultipartFile file : car.getImageList()) {
					if (file != null && StringUtils.hasText(file.getOriginalFilename())) {
						String fileOrgName = file.getOriginalFilename();
						String modifiedFileName = FilenameUtils.getBaseName(fileOrgName) + ","
								+ System.currentTimeMillis() + "," + FilenameUtils.getExtension(fileOrgName);
						File storeFile = uploadService.getFilePath(modifiedFileName, "images");
						if (storeFile != null) {
							try {
								FileUtils.writeByteArrayToFile(storeFile, file.getBytes());
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						Image image = new Image();
						image.setFileExtension(FilenameUtils.getExtension(fileOrgName));
						image.setFileName(fileOrgName);
						image.setModifiedFileName(modifiedFileName);
						image.setCar(car);
						ImgRep.save(image);
					}
				}
			}
			ImgRep.deleteImagesFromDB(car.getId(), deleteImagesByte(car));
			return "success";
		} else {
			return "Neka polja su ostala prazna." + "\n"
					+ "Obavezna polja su: MARKA, MODEL, PRVA REGISTRACIJA(GODINA PROIZVODNJE) I BROJ ŠASIJE ";
		}
	}

	@Override
	public List<Car> getAllVehicles() {
		List<Car> listOfCar = new ArrayList<>();
		List<Car> allCars = carRepository.findAll();
		for (Car car : allCars) {
			Date currentDate = new Date();
			Date regValid = car.getRegValid();
			long diff = regValid.getTime() - currentDate.getTime();
			long diffDays = diff / (24 * 60 * 60 * 1000);
			car.setDayToReg(diffDays);

			DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
			Date date = car.getRegValid();
			String viewDate = dateFormat.format(date);
			car.setViewDate(viewDate);

			listOfCar.add(car);
		}

		return listOfCar;

	}

	@Override
	public Car getCar(Long vehicleId) {
		return carRepository.findById(vehicleId).get();

	}

	@Override
	public boolean deleteVehicleById(Long vehicleId) {
		List<Image> images = ImgRep.findAllById(vehicleId);
		List<String> delList = new ArrayList<>();
		for (Image image : images) {
			String name = image.getModifiedFileName();
			delList.add(name);
		}
		Car car = new Car();
		car.setRemoveImages(delList);

		deleteAllImagesByte(car);
		ImgRep.deleteAllImagesByVehicle(vehicleId);
		carRepository.deleteById(vehicleId);

		return true;
	}

	// -------------------------Owner-----------------------------

	@Override
	public boolean saveOwner(Owner owner) {
		if (owner.getOwnerName().equals("") && owner.getName().equals("") && owner.getAddress().equals("")
				&& owner.getJmbg().equals("")) {
			return false;
		} else {
			ownerRepository.save(owner);
			return true;
		}

	}

	@Override
	public List<Owner> getAllOwners() {
		return ownerRepository.findAll();

	}

	@Override
	public boolean deleteOwnerById(Long ownerId) {
		ownerRepository.deleteById(ownerId);
		return true;
	}

	@Override
	public Owner getOneOfOwner(Long ownerId) {
		return ownerRepository.findById(ownerId).get();

	}

	// --------------METHOD FOR DELETE IMAGES FROM SERVER---------

	public String[] deleteImagesByte(Car car) {
		String[] arrayForDelete = new String[10];
		if (car != null && car.getRemoveImages() != null && car.getRemoveImages().size() > 0) {
			// Read array string from JavaScript array and parse...
			String nameFromImage = "";
			int posImgIn = 0;
			for (String nameImgFromJS : car.getRemoveImages()) {
				if (nameImgFromJS.equals("jpg") || nameImgFromJS.equals("JPG") || nameImgFromJS.equals("jpeg")
						|| nameImgFromJS.equals("JPEG") || nameImgFromJS.equals("png") || nameImgFromJS.equals("PNG")
						|| nameImgFromJS.equals("jfif") || nameImgFromJS.equals("JFIF")) {
					nameFromImage = nameFromImage + nameImgFromJS;
					arrayForDelete[posImgIn] = nameFromImage;
					posImgIn++;
					nameFromImage = "";
				} else {
					nameFromImage = nameFromImage + nameImgFromJS + ",";
				}
			}
			// checking in file and delete file
			for (String fileForDelete : arrayForDelete) {
				File dbFile = new File(context.getRealPath("/images/" + fileForDelete));
				if (dbFile.exists()) {
					dbFile.delete();
				}
			}
		}
		return arrayForDelete;
	}

	public void deleteAllImagesByte(Car car) {
		if (car != null && car.getRemoveImages() != null && car.getRemoveImages().size() > 0) {
			for (String delete : car.getRemoveImages()) {
				File dbFile = new File(context.getRealPath("/images/" + delete));
				if (dbFile.exists()) {
					dbFile.delete();
				}
			}
		}
	}
	// ----------------END DELETE METHOD---------------

	// ----------DOWNLOAD PDF--------------------------

	@Override
	public boolean createdPdf(Car car, List<ServiceBook> service, ServletContext context, HttpServletRequest request,
			HttpServletResponse response) {
		Document document = new Document(PageSize.A4, 15, 15, 45, 30);
		try {

			String filePath = context.getRealPath("/resources/reports");
			File file = new File(filePath);
			boolean exists = new File(filePath).exists();
			if (!exists) {
				new File(filePath).mkdirs();
			}
			PdfWriter writer = PdfWriter.getInstance(document,
					new FileOutputStream(file + "/" + "Vozilo-administrator" + ".pdf"));
			document.open();

			// ---TITLE AND SUBTITLE---
			Font title = FontFactory.getFont("Arial", 12, Font.BOLD);
			Font subtitle = FontFactory.getFont("Arial", 10, BaseColor.BLACK);
			Paragraph par = new Paragraph("COSIC PODOVI DOO", title);
			par.setAlignment(Element.ALIGN_CENTER);
			par.setIndentationLeft(50);
			par.setIndentationRight(50);
			par.setSpacingAfter(5);
			Paragraph parLincensePlate = new Paragraph("Vozilo: " + car.getLicensePlate(), subtitle);
			parLincensePlate.setAlignment(Element.ALIGN_CENTER);
			parLincensePlate.setIndentationLeft(50);
			parLincensePlate.setIndentationRight(50);
			parLincensePlate.setSpacingAfter(10);
			document.add(par);
			document.add(parLincensePlate);

			// ---MAIN TABLE---
			PdfPTable mainTable = new PdfPTable(3);
			mainTable.setWidthPercentage(100);
			mainTable.setSpacingBefore(5f);
			mainTable.setSpacingAfter(20f);

			float[] mainColumnWidths = { 3f, 3f, 3f };
			mainTable.setWidths(mainColumnWidths);

			Font mainTableHeader = FontFactory.getFont("Arial", 14, BaseColor.BLACK);

			PdfPCell headOwner = new PdfPCell(new Paragraph("Vlasnik", mainTableHeader));
			headOwner.setBorderColor(BaseColor.BLACK);
			headOwner.setPaddingLeft(10);
			headOwner.setHorizontalAlignment(Element.ALIGN_CENTER);
			headOwner.setVerticalAlignment(Element.ALIGN_CENTER);
			headOwner.setBackgroundColor(BaseColor.GRAY);
			headOwner.setExtraParagraphSpace(5f);

			mainTable.addCell(headOwner);

			PdfPCell headMotor = new PdfPCell(new Paragraph("Motor", mainTableHeader));
			headMotor.setBorderColor(BaseColor.BLACK);
			headMotor.setPaddingLeft(10);
			headMotor.setHorizontalAlignment(Element.ALIGN_CENTER);
			headMotor.setVerticalAlignment(Element.ALIGN_CENTER);
			headMotor.setBackgroundColor(BaseColor.GRAY);
			headMotor.setExtraParagraphSpace(5f);

			mainTable.addCell(headMotor);

			PdfPCell headChassis = new PdfPCell(new Paragraph("Sasija", mainTableHeader));
			headChassis.setBorderColor(BaseColor.BLACK);
			headChassis.setPaddingLeft(10);
			headChassis.setHorizontalAlignment(Element.ALIGN_CENTER);
			headChassis.setVerticalAlignment(Element.ALIGN_CENTER);
			headChassis.setBackgroundColor(BaseColor.GRAY);
			headChassis.setExtraParagraphSpace(5f);

			mainTable.addCell(headChassis);

			// --- SECOND TABLE OWNER---

			PdfPTable tableOwner = new PdfPTable(1);
			tableOwner.setWidthPercentage(100);
			tableOwner.setSpacingBefore(1f);
			tableOwner.setSpacingAfter(1f);

			Font tableBody = FontFactory.getFont("Arial", 10, BaseColor.BLACK);

			float[] columnWidths = { 1f };
			tableOwner.setWidths(columnWidths);

			PdfPCell ownerNameValue = new PdfPCell(
					new Paragraph("Vlasnik: " + car.getOwner().getOwnerName(), tableBody));
			ownerNameValue.setBorderColor(BaseColor.BLACK);
			ownerNameValue.setPaddingLeft(10);
			ownerNameValue.setHorizontalAlignment(Element.ALIGN_CENTER);
			ownerNameValue.setVerticalAlignment(Element.ALIGN_CENTER);
			ownerNameValue.setBackgroundColor(BaseColor.WHITE);
			ownerNameValue.setExtraParagraphSpace(5f);
			tableOwner.addCell(ownerNameValue);

			PdfPCell nameValue = new PdfPCell(new Paragraph("Ime i prezime: " + car.getOwner().getName(), tableBody));
			nameValue.setBorderColor(BaseColor.BLACK);
			nameValue.setPaddingLeft(10);
			nameValue.setHorizontalAlignment(Element.ALIGN_CENTER);
			nameValue.setVerticalAlignment(Element.ALIGN_CENTER);
			nameValue.setBackgroundColor(BaseColor.WHITE);
			nameValue.setExtraParagraphSpace(5f);
			tableOwner.addCell(nameValue);

			PdfPCell addressValue = new PdfPCell(new Paragraph("Adresa: " + car.getOwner().getAddress(), tableBody));
			addressValue.setBorderColor(BaseColor.BLACK);
			addressValue.setPaddingLeft(10);
			addressValue.setHorizontalAlignment(Element.ALIGN_CENTER);
			addressValue.setVerticalAlignment(Element.ALIGN_CENTER);
			addressValue.setBackgroundColor(BaseColor.WHITE);
			addressValue.setExtraParagraphSpace(5f);
			tableOwner.addCell(addressValue);

			PdfPCell JMBGValue = new PdfPCell(new Paragraph("JMBG: " + car.getOwner().getJmbg(), tableBody));
			JMBGValue.setBorderColor(BaseColor.BLACK);
			JMBGValue.setPaddingLeft(10);
			JMBGValue.setHorizontalAlignment(Element.ALIGN_CENTER);
			JMBGValue.setVerticalAlignment(Element.ALIGN_CENTER);
			JMBGValue.setBackgroundColor(BaseColor.WHITE);
			JMBGValue.setExtraParagraphSpace(5f);
			tableOwner.addCell(JMBGValue);

			mainTable.addCell(tableOwner);

			// --- SECOND TABLE MOTOR--

			PdfPTable tableMotor = new PdfPTable(1);
			tableMotor.setWidthPercentage(100);
			tableMotor.setSpacingBefore(1f);
			tableMotor.setSpacingAfter(1f);

			Font tableBodyMotor = FontFactory.getFont("Arial", 10, BaseColor.BLACK);

			float[] columnWidthsMotor = { 2f };
			tableMotor.setWidths(columnWidthsMotor);

			PdfPCell volumeValue = new PdfPCell(
					new Paragraph("Zapremina: " + car.getVolume() + " ccm", tableBodyMotor));
			volumeValue.setBorderColor(BaseColor.BLACK);
			volumeValue.setPaddingLeft(10);
			volumeValue.setHorizontalAlignment(Element.ALIGN_CENTER);
			volumeValue.setVerticalAlignment(Element.ALIGN_CENTER);
			volumeValue.setBackgroundColor(BaseColor.WHITE);
			volumeValue.setExtraParagraphSpace(5f);
			tableMotor.addCell(volumeValue);

			PdfPCell fuelValue = new PdfPCell(new Paragraph("Vrsta goriva: " + car.getFuelType(), tableBodyMotor));
			fuelValue.setBorderColor(BaseColor.BLACK);
			fuelValue.setPaddingLeft(10);
			fuelValue.setHorizontalAlignment(Element.ALIGN_CENTER);
			fuelValue.setVerticalAlignment(Element.ALIGN_CENTER);
			fuelValue.setBackgroundColor(BaseColor.WHITE);
			fuelValue.setExtraParagraphSpace(5f);
			tableMotor.addCell(fuelValue);

			PdfPCell powerValue = new PdfPCell(new Paragraph("Snaga: " + car.getPower() + " kW", tableBodyMotor));
			powerValue.setBorderColor(BaseColor.BLACK);
			powerValue.setPaddingLeft(10);
			powerValue.setHorizontalAlignment(Element.ALIGN_CENTER);
			powerValue.setVerticalAlignment(Element.ALIGN_CENTER);
			powerValue.setBackgroundColor(BaseColor.WHITE);
			powerValue.setExtraParagraphSpace(5f);
			tableMotor.addCell(powerValue);

			PdfPCell numberValue = new PdfPCell(new Paragraph("Broj motora: " + car.getMotorNumber(), tableBodyMotor));
			numberValue.setBorderColor(BaseColor.BLACK);
			numberValue.setPaddingLeft(10);
			numberValue.setHorizontalAlignment(Element.ALIGN_CENTER);
			numberValue.setVerticalAlignment(Element.ALIGN_CENTER);
			numberValue.setBackgroundColor(BaseColor.WHITE);
			numberValue.setExtraParagraphSpace(5f);
			tableMotor.addCell(numberValue);

			mainTable.addCell(tableMotor);

			// ---SECOND TABLE CHASSIS

			PdfPTable tableChassis = new PdfPTable(1);
			tableChassis.setWidthPercentage(100);
			tableChassis.setSpacingBefore(1f);
			tableChassis.setSpacingAfter(1f);

			Font tableBodyChassis = FontFactory.getFont("Arial", 10, BaseColor.BLACK);

			float[] columnWidthsChassis = { 2f };
			tableChassis.setWidths(columnWidthsChassis);

			PdfPCell firstRegValue = new PdfPCell(
					new Paragraph("Prva registracija: " + car.getFirstReg(), tableBodyChassis));
			firstRegValue.setBorderColor(BaseColor.BLACK);
			firstRegValue.setPaddingLeft(10);
			firstRegValue.setHorizontalAlignment(Element.ALIGN_CENTER);
			firstRegValue.setVerticalAlignment(Element.ALIGN_CENTER);
			firstRegValue.setBackgroundColor(BaseColor.WHITE);
			firstRegValue.setExtraParagraphSpace(5f);
			tableChassis.addCell(firstRegValue);

			PdfPCell placeValue = new PdfPCell(new Paragraph("Broj mesta: " + car.getPlace(), tableBodyChassis));
			placeValue.setBorderColor(BaseColor.BLACK);
			placeValue.setPaddingLeft(10);
			placeValue.setHorizontalAlignment(Element.ALIGN_CENTER);
			placeValue.setVerticalAlignment(Element.ALIGN_CENTER);
			placeValue.setBackgroundColor(BaseColor.WHITE);
			placeValue.setExtraParagraphSpace(5f);
			tableChassis.addCell(placeValue);

			PdfPCell massValue = new PdfPCell(new Paragraph("Masa vozila: " + car.getMass() + " kg", tableBodyChassis));
			massValue.setBorderColor(BaseColor.BLACK);
			massValue.setPaddingLeft(10);
			massValue.setHorizontalAlignment(Element.ALIGN_CENTER);
			massValue.setVerticalAlignment(Element.ALIGN_CENTER);
			massValue.setBackgroundColor(BaseColor.WHITE);
			massValue.setExtraParagraphSpace(5f);
			tableChassis.addCell(massValue);

			PdfPCell payloadValue = new PdfPCell(
					new Paragraph("Nosivost: " + car.getPayload() + " kg", tableBodyChassis));
			payloadValue.setBorderColor(BaseColor.BLACK);
			payloadValue.setPaddingLeft(10);
			payloadValue.setHorizontalAlignment(Element.ALIGN_CENTER);
			payloadValue.setVerticalAlignment(Element.ALIGN_CENTER);
			payloadValue.setBackgroundColor(BaseColor.WHITE);
			payloadValue.setExtraParagraphSpace(5f);
			tableChassis.addCell(payloadValue);

			PdfPCell categoryValue = new PdfPCell(
					new Paragraph("Kategorija vozila: " + car.getCategory(), tableBodyChassis));
			categoryValue.setBorderColor(BaseColor.BLACK);
			categoryValue.setPaddingLeft(10);
			categoryValue.setHorizontalAlignment(Element.ALIGN_CENTER);
			categoryValue.setVerticalAlignment(Element.ALIGN_CENTER);
			categoryValue.setBackgroundColor(BaseColor.WHITE);
			categoryValue.setExtraParagraphSpace(5f);
			tableChassis.addCell(categoryValue);

			PdfPCell numberChassisValue = new PdfPCell(
					new Paragraph("Broj sasije: " + car.getChassisNumber(), tableBodyChassis));
			numberChassisValue.setBorderColor(BaseColor.BLACK);
			numberChassisValue.setPaddingLeft(10);
			numberChassisValue.setHorizontalAlignment(Element.ALIGN_CENTER);
			numberChassisValue.setVerticalAlignment(Element.ALIGN_CENTER);
			numberChassisValue.setBackgroundColor(BaseColor.WHITE);
			numberChassisValue.setExtraParagraphSpace(5f);
			tableChassis.addCell(numberChassisValue);
			mainTable.addCell(tableChassis);

			// ---END SECOND TABLE--

			// --- SERVICE PART
			Font titleService = FontFactory.getFont("Arial", 12, Font.BOLD);
			Font subtitleService = FontFactory.getFont("Arial", 10, BaseColor.BLACK);

			Paragraph parService = new Paragraph("Svi servisi vezani za navedeno vozilo", titleService);
			parService.setAlignment(Element.ALIGN_CENTER);
			parService.setIndentationLeft(50);
			parService.setIndentationRight(50);
			parService.setSpacingAfter(10);

			double summeService = 0;
			for (ServiceBook serviceBookPrice : service) {
				summeService = summeService + serviceBookPrice.getPrice();
			}

			Paragraph parSumme = new Paragraph(
					"Ukupni troškovi dosadašnjeg održavanja vozila: " + String.valueOf(summeService) + " RSD",
					subtitleService);
			parSumme.setAlignment(Element.ALIGN_LEFT);
			parSumme.setIndentationLeft(5);
			parSumme.setIndentationRight(50);
			parSumme.setSpacingAfter(2);

			// ---SERVICE TABLE HEADER---
			PdfPTable mainTableService = new PdfPTable(5);
			mainTableService.setWidthPercentage(100);
			mainTableService.setSpacingBefore(5f);
			mainTableService.setSpacingAfter(10f);

			float[] mainColumnWidthsService = { 3f, 3f, 3f, 3f, 3f };
			mainTableService.setWidths(mainColumnWidthsService);

			Font mainTableHeaderService = FontFactory.getFont("Arial", 14, BaseColor.BLACK);

			PdfPCell dateService = new PdfPCell(new Paragraph("Datum", mainTableHeaderService));
			dateService.setBorderColor(BaseColor.BLACK);
			dateService.setPaddingLeft(10);
			dateService.setHorizontalAlignment(Element.ALIGN_CENTER);
			dateService.setVerticalAlignment(Element.ALIGN_CENTER);
			dateService.setBackgroundColor(BaseColor.GRAY);
			dateService.setExtraParagraphSpace(5f);

			mainTableService.addCell(dateService);

			PdfPCell workService = new PdfPCell(new Paragraph("Opis", mainTableHeaderService));
			workService.setBorderColor(BaseColor.BLACK);
			workService.setPaddingLeft(10);
			workService.setHorizontalAlignment(Element.ALIGN_CENTER);
			workService.setVerticalAlignment(Element.ALIGN_CENTER);
			workService.setBackgroundColor(BaseColor.GRAY);
			workService.setExtraParagraphSpace(5f);

			mainTableService.addCell(workService);

			PdfPCell workshopService = new PdfPCell(new Paragraph("Auto servis", mainTableHeaderService));
			workshopService.setBorderColor(BaseColor.BLACK);
			workshopService.setPaddingLeft(10);
			workshopService.setHorizontalAlignment(Element.ALIGN_CENTER);
			workshopService.setVerticalAlignment(Element.ALIGN_CENTER);
			workshopService.setBackgroundColor(BaseColor.GRAY);
			workshopService.setExtraParagraphSpace(5f);

			mainTableService.addCell(workshopService);

			PdfPCell kmService = new PdfPCell(new Paragraph("Kilometraža", mainTableHeaderService));
			kmService.setBorderColor(BaseColor.BLACK);
			kmService.setPaddingLeft(10);
			kmService.setHorizontalAlignment(Element.ALIGN_CENTER);
			kmService.setVerticalAlignment(Element.ALIGN_CENTER);
			kmService.setBackgroundColor(BaseColor.GRAY);
			kmService.setExtraParagraphSpace(5f);

			mainTableService.addCell(kmService);

			PdfPCell priceService = new PdfPCell(new Paragraph("Cena", mainTableHeaderService));
			priceService.setBorderColor(BaseColor.BLACK);
			priceService.setPaddingLeft(10);
			priceService.setHorizontalAlignment(Element.ALIGN_CENTER);
			priceService.setVerticalAlignment(Element.ALIGN_CENTER);
			priceService.setBackgroundColor(BaseColor.GRAY);
			priceService.setExtraParagraphSpace(5f);

			mainTableService.addCell(priceService);

			Font mainTableBodyService = FontFactory.getFont("Arial", 12, BaseColor.BLACK);

			for (ServiceBook serviceBook : service) {

				PdfPCell dateServiceValue = new PdfPCell(
						new Paragraph(serviceBook.getDate().toString(), mainTableBodyService));
				dateServiceValue.setBorderColor(BaseColor.BLACK);
				dateServiceValue.setPaddingLeft(10);
				dateServiceValue.setHorizontalAlignment(Element.ALIGN_CENTER);
				dateServiceValue.setVerticalAlignment(Element.ALIGN_CENTER);
				dateServiceValue.setBackgroundColor(BaseColor.WHITE);
				dateServiceValue.setExtraParagraphSpace(5f);

				mainTableService.addCell(dateServiceValue);

				PdfPCell workServiceValue = new PdfPCell(new Paragraph(serviceBook.getWork(), mainTableBodyService));
				workServiceValue.setBorderColor(BaseColor.BLACK);
				workServiceValue.setPaddingLeft(10);
				workServiceValue.setHorizontalAlignment(Element.ALIGN_CENTER);
				workServiceValue.setVerticalAlignment(Element.ALIGN_CENTER);
				workServiceValue.setBackgroundColor(BaseColor.WHITE);
				workServiceValue.setExtraParagraphSpace(5f);

				mainTableService.addCell(workServiceValue);

				PdfPCell workshopServiceValue = new PdfPCell(
						new Paragraph(serviceBook.getWorkshop(), mainTableBodyService));
				workshopServiceValue.setBorderColor(BaseColor.BLACK);
				workshopServiceValue.setPaddingLeft(10);
				workshopServiceValue.setHorizontalAlignment(Element.ALIGN_CENTER);
				workshopServiceValue.setVerticalAlignment(Element.ALIGN_CENTER);
				workshopServiceValue.setBackgroundColor(BaseColor.WHITE);
				workshopServiceValue.setExtraParagraphSpace(5f);

				mainTableService.addCell(workshopServiceValue);

				PdfPCell kmServiceValue = new PdfPCell(
						new Paragraph(String.valueOf(serviceBook.getKm()), mainTableBodyService));
				kmServiceValue.setBorderColor(BaseColor.BLACK);
				kmServiceValue.setPaddingLeft(10);
				kmServiceValue.setHorizontalAlignment(Element.ALIGN_CENTER);
				kmServiceValue.setVerticalAlignment(Element.ALIGN_CENTER);
				kmServiceValue.setBackgroundColor(BaseColor.WHITE);
				kmServiceValue.setExtraParagraphSpace(5f);

				mainTableService.addCell(kmServiceValue);

				PdfPCell priceServiceValue = new PdfPCell(
						new Paragraph(String.valueOf(serviceBook.getPrice()), mainTableBodyService));
				priceServiceValue.setBorderColor(BaseColor.BLACK);
				priceServiceValue.setPaddingLeft(10);
				priceServiceValue.setHorizontalAlignment(Element.ALIGN_CENTER);
				priceServiceValue.setVerticalAlignment(Element.ALIGN_CENTER);
				priceServiceValue.setBackgroundColor(BaseColor.WHITE);
				priceServiceValue.setExtraParagraphSpace(5f);

				mainTableService.addCell(priceServiceValue);

			}

			// ---- SERVICE TABLE HEADER---

			document.add(mainTable);
			document.add(parService);
			document.add(parSumme);
			document.add(mainTableService);
			document.close();
			writer.close();
			return true;
		} catch (Exception e) {
			return false;
		}

	}

}
