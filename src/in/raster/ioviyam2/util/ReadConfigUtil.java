package in.raster.ioviyam2.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ReadConfigUtil {
	public static Map<String, String> readConfig(String aeConfig,
			String hostConfig, String portConfig, String wadoConfig, String path) {
		if (null == aeConfig || null == hostConfig || null == portConfig
				|| null == wadoConfig) {
			Properties prop = new Properties();
			Map<String, String> configMap = new HashMap<String, String>();
			try {
				// 读取配置
				File filedata = new File(path + "iOviyam.properties");
				prop.load(new FileInputStream(filedata));
				configMap.put("ae", prop.getProperty("AETitle"));
				configMap.put("host", prop.getProperty("Host"));
				configMap.put("port", prop.getProperty("Port"));
				configMap.put("wado", prop.getProperty("wado"));
				return configMap;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
