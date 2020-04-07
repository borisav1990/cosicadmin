package registry.service;

import registry.models.Car;

public class CheckCar {

	public boolean checkField(Car car) {
		if (car.getBrand().equals("default")) {
			return false;
		}
		if (car.getModel().equals("")) {
			return false;
		}
		if (car.getFirstReg().equals("default")) {
			return false;
		}
		if (car.getChassisNumber().equals("")) {
			return false;

		} else {
			return true;
		}

	}

}
