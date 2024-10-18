package org.qrush.brand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
    @RequestMapping("/brands")
    public class BrandController {

        @Autowired
        private BrandService brandService;

        @GetMapping("/hello")
        public String sayHello(@RequestParam(value = "myName", defaultValue = "World") String name) {
            return String.format("Hello biscuit %s!", name);
        }

        @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
        public Brand createBrand(@RequestBody Brand brand) {
            return brandService.createBrand(brand);
        }

        @GetMapping
        public List<Brand> getAllBrands() {
            return brandService.getAllBrands();
        }

//	@PostMapping(path = "/brands",
//			consumes = MediaType.APPLICATION_JSON_VALUE)
//	public String createUser(@RequestBody Brand newBrand) {
//		String brandName = newBrand.getName();
//		return "HI " + brandName;
//	}
    }



/*

	@PostMapping("/restaurant/menu")
	public Map<String, String> createMenu(@RequestParam(value = "menu", defaultValue = "Food") String menu) {
		HashMap<String, String> map = new HashMap<>();
		map.put("menu", menu);
		map.put("type", "food");
		return map;
	}

	@PostMapping("/restaurant/oldMenu.csv")
	public void menuAsCSV(HttpServletResponse response) throws IOException {
		var menu = "dish_name,price\nlamb,6\nbeef,7\n";
		response.setContentType("text/plain; charset=utf-8");
		response.getWriter().println(menu);
	}


	@PostMapping("/restaurant/menu.csv")
	public void menuAsCSV(@RequestParam(value = "menu", defaultValue = "dish_name,price\nlamb,6\nbeef,7\n") String menu) throws IOException {
		List<String[]> itemList = new ArrayList<>();

		var entries = menu.split("\\r?\\n");
		for (String entry : entries) {
			String[] parts = entry.split(",");
			itemList.add(parts);
		}

		File csvOutputFile = new File("test.csv");

		try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
			itemList.stream()
					.map(this::convertToCSV)
					.forEach(pw::println);
		}

		//assertTrue(csvOutputFile.exists());
    }

	private String convertToCSV(String[] data) {
		return Stream.of(data)
				.map(this::escapeSpecialCharacters)
				.collect(Collectors.joining(","));
	}

	private String escapeSpecialCharacters(String data) {
		if (data == null) {
			throw new IllegalArgumentException("Input data cannot be null");
		}
		String escapedData = data.replaceAll("\\R", " ");
		if (data.contains(",") || data.contains("\"") || data.contains("'")) {
			data = data.replace("\"", "\"\"");
			escapedData = "\"" + data + "\"";
		}
		return escapedData;
	}

	//upload csv data as string
	//parse into .csv
	//return .csv

	//e.g
	//input:
	//"dish_name,price\nlamb,6\nbeef,7"
	//output:
	//dish_name,price
	//lamb,6
	//beef,7
*/
