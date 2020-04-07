package registry.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

@Entity
@Table(name = "cars")
public class Car {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	@Column(name = "brand")
	private String brand;

	@Column(name = "model")
	private String model;

	@Column(name = "lincensePlate")
	private String licensePlate;

	// @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	@Column(name = "regValid")
	private Date regValid;

	// ----------------------------------

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "owner_id")
	private Owner owner;

	@Column(name = "firstReg")
	private String firstReg;

	@Column(name = "numberReg")
	private String numberReg;

	@Column(name = "fuelType")
	private String fuelType;

	// -----------------------------------

	@Column(name = "place")
	private String place;

	@Column(name = "volume")
	private String volume;

	@Column(name = "power")
	private String power;

	@Column(name = "mass")
	private String mass;

	@Column(name = "payload")
	private String payload;

	// -------------------------------------

	@Column(name = "category")
	private String category;

	@Column(name = "chassisNumber")
	private String chassisNumber;

	@Column(name = "motorNumber")
	private String motorNumber;

	// ---------@Transient-------------------

	@Transient
	private long dayToReg;

	@Transient
	private String viewDate;

	// ----------------------------------

	@Transient
	private List<MultipartFile> imageList = new ArrayList<MultipartFile>();

	@Transient
	private List<String> removeImages = new ArrayList<String>();

	// ---------------------------------------------------------

	public Long getId() {
		return id;
	}

	public Car() {
		super();
	}

	public List<MultipartFile> getImageList() {
		return imageList;
	}

	public void setImageList(List<MultipartFile> imageList) {
		this.imageList = imageList;
	}

	public List<String> getRemoveImages() {
		return removeImages;
	}

	public void setRemoveImages(List<String> removeImages) {
		this.removeImages = removeImages;
	}

	public String getViewDate() {
		return viewDate;
	}

	public void setViewDate(String viewDate) {
		this.viewDate = viewDate;
	}

	public long getDayToReg() {
		return dayToReg;
	}

	public void setDayToReg(long dayToReg) {
		this.dayToReg = dayToReg;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getLicensePlate() {
		return licensePlate;
	}

	public void setLicensePlate(String licensePlate) {
		this.licensePlate = licensePlate;
	}

	public Date getRegValid() {
		return regValid;
	}

	public void setRegValid(Date regValid) {
		this.regValid = regValid;
	}

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	public String getFirstReg() {
		return firstReg;
	}

	public void setFirstReg(String firstReg) {
		this.firstReg = firstReg;
	}

	public String getNumberReg() {
		return numberReg;
	}

	public void setNumberReg(String numberReg) {
		this.numberReg = numberReg;
	}

	public String getFuelType() {
		return fuelType;
	}

	public void setFuelType(String fuelType) {
		this.fuelType = fuelType;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getPower() {
		return power;
	}

	public void setPower(String power) {
		this.power = power;
	}

	public String getMass() {
		return mass;
	}

	public void setMass(String mass) {
		this.mass = mass;
	}

	public String getPayload() {
		return payload;
	}

	public void setPayload(String payload) {
		this.payload = payload;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getChassisNumber() {
		return chassisNumber;
	}

	public void setChassisNumber(String chassisNumber) {
		this.chassisNumber = chassisNumber;
	}

	public String getMotorNumber() {
		return motorNumber;
	}

	public void setMotorNumber(String motorNumber) {
		this.motorNumber = motorNumber;
	}

}
