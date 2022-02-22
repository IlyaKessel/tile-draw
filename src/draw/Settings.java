package draw;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Settings {

	private static final String SETTINGS_FILE = "projectSettings";

	public List<String> getColors() {
		Map<Object, Object> jsn = getJson();
		if(jsn.containsKey("colors")) {
			String colors = (String) jsn.get("colors");
			return Arrays.asList(colors.split(";"));
		}
		return new ArrayList<String>();
	}

	public void saveColors(ArrayList<String> strs) {
		String joined = String.join(";", strs);
		Map<Object, Object> jsn = getJson();
		jsn.put("colors", joined);
		save(jsn);
	}
	
	public void save(Map<Object, Object> data) {
		try {
			
			ObjectMapper mapper = new ObjectMapper();
			String str = mapper.writeValueAsString(data);
			try (PrintWriter out = new PrintWriter(SETTINGS_FILE)) {
			    out.println(str);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Map<Object,Object> getJson() {
		if(new File(SETTINGS_FILE).isFile()) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				Map<Object, Object> map = mapper.readValue(Paths.get(SETTINGS_FILE).toFile(), Map.class);
				return map;

			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new HashMap<Object, Object>();
	}

}
