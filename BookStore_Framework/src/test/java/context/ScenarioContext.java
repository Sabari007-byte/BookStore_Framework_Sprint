package context;


import java.util.HashMap;
import java.util.Map;

public class ScenarioContext {
	

    public Map<String, Object> data = new HashMap<>();

    public void set(String key, Object value) {
        data.put(key, value);
    }

    public Object get(String key) {
        return data.get(key);
    }
}