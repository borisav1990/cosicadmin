package registry.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController implements ErrorController {
	private static final String PATH = "/error";

	@Override
	public String getErrorPath() { // Ovo je ErrorController - nuzna metora i ona ce da mi vrati "/error"

		return PATH;
	}

	@RequestMapping(PATH)
	public String error() {
		return "error";

	}

}
