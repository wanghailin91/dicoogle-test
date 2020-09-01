package in.raster.ioviyam2.util;

import java.io.File;

import org.apache.log4j.Logger;
import org.dcm4che2.tool.dcmsnd.DcmSnd;

public class DicomSendUtil {

	private static Logger log = Logger.getLogger(DicomSendUtil.class);

	public boolean sendBySelf(String address,String dir) {
		DcmSnd dcmsnd = new DcmSnd("DCMSND");
		try {
			String[] calledAETAddress = split(address, '@');
			dcmsnd.setCalledAET(calledAETAddress[0]);

			String[] hostPort = split(calledAETAddress[1], ':');
			dcmsnd.setRemoteHost(hostPort[0]);
			dcmsnd.setRemotePort(toPort(hostPort[1]));
		    dcmsnd.addFile(new File(dir));
		    if (dcmsnd.getNumberOfFilesToSend() > 0) {
		    	dcmsnd.configureTransferCapability();
		    	dcmsnd.start();
		    	dcmsnd.open();
		    	dcmsnd.send();
		    	dcmsnd.close();
		    }
		    return true;
		} catch (Exception e) {
			log.error(e.getMessage());			
			return false;
		}finally {
		      dcmsnd.stop();
	    }
	}
	
	
	
	public void deleteTempFiles(String dir){
		File tempFile=new File(dir);
 		File[] files=tempFile.listFiles();
		for (File file : files) {
			file.delete();
		}
		//删除目录
		tempFile.delete();
	}

	private static int toPort(String port) {
		return port != null ? 
				parseInt(port, "illegal port number", 1, 65535): 104;
	}

	private static int parseInt(String s, String errPrompt, int min, int max) {
		try {
			int i = Integer.parseInt(s);
			if ((i >= min) && (i <= max))
				return i;
		} catch (NumberFormatException e) {
		}
		throw new RuntimeException();
	}

	private static String[] split(String s, char delim) {
		String[] s2 = { s, null };
		int pos = s.indexOf(delim);
		if (pos != -1) {
			s2[0] = s.substring(0, pos);
			s2[1] = s.substring(pos + 1);
		}
		return s2;
	}
	
}
