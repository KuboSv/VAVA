package com.server;

import java.sql.*;
import java.util.*;
import javax.ejb.*;
import javax.naming.*;
import javax.sql.DataSource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.server.entity.*;
import com.sun.corba.se.spi.activation.Server;


/**
 * @author Jakub Juško, Ivan Petrov
 */

@Stateless
@LocalBean
@Remote(ServerBeanRemote.class)
public class ServerBean implements ServerBeanRemote {

	/**
	 * Metoda ktora nacita z databazy otazky a odpovede podla kategoria
	 * @param	kategoria_id predstavuje index kategorie
	 * @return	zoznam otazok a odpovedi 
	 * @see		Question
	 */
	public List<Question> getOtazky(int kategoria_id) {
		
		List<Question> otazky = new ArrayList<Question>();

		Connection conn = null;
		
		
		String otazka = new String();
		String odpoved = new String();
		boolean spr = false;

		
		try {
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:jboss/datasources/PostgresDS");
			conn = (Connection) ds.getConnection();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("databaza test");
		}

		try {

			PreparedStatement stmt = conn.prepareStatement("select o.text_otazky,o2.text_odpovede,o2.spravna from odpoved o2 "
					+"join otazky o on o2.otazka_id = o.otazky_id "
					 +"join kategoria k on o.kategoria_id = k.kategoria_id "
					+"where o.kategoria_id = ? order by otazky_id asc;");
			
			stmt.setInt(1, kategoria_id);
			
			ResultSet rs = stmt.executeQuery();
	
			while (rs.next()) {
				
				Question o = new Question();
				
				o.setQuestion(rs.getString(1));
				o.setAnswer(rs.getString(2));
				o.setCorrect(rs.getBoolean(3));
			   
				otazky.add(o);
				System.out.println(otazky.size());
			}

			stmt.close();
			conn.close();

		} catch (SQLException e) {

			e.printStackTrace();
		}
	
		return otazky;
	}

	/**
	 * Metoda ktora prida do databazy , tabulky rebricek noveho hraca
	 * @param	meno predstavuje meno hraca
	 * @param	body pocet ziskanych bodov
	 * @see		TopPlayers
	 */
	public void pridaj(TopPlayers hrac) {

		Connection conn = null;
		try {
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:jboss/datasources/PostgresDS");
			conn = (Connection) ds.getConnection();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("databaza test");
		}

		try {

			PreparedStatement stmt = conn.prepareStatement("insert into rebricek (meno, body) values ('"+hrac.getName()+"',"+hrac.getScore()+");");
			 stmt.executeUpdate();
			
			stmt.close();
			conn.close();

		} catch (SQLException e) {

			e.printStackTrace();
		}
	}

	
	/**
	 * Metoda ktora vrati zoznam top hracov a ich body z databazy
	 * @return	zoznam hracov nachadzajucich sa v rebricku
	 * @see		TopPlayers
	 */
	public List<TopPlayers> getTopPlayers() {
		
		List<TopPlayers> hraci = new ArrayList<TopPlayers>();
		
		Connection conn = null;
		
		String meno = new String();
		int body = 0;
		
		try {
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:jboss/datasources/PostgresDS");
			conn = (Connection) ds.getConnection();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("databaza test");
		}

		try {

			PreparedStatement stmt = conn.prepareStatement("select * from rebricek;");
			ResultSet rs = stmt.executeQuery();
	
			while (rs.next()) {
				
				TopPlayers top = new TopPlayers();
				
				top.setName(rs.getString(1));
				top.setScore(rs.getInt(2));
				hraci.add(top);
			}

			stmt.close();
			conn.close();

		} catch (SQLException e) {

			e.printStackTrace();
		}
	
		return hraci;
	}

	public void SentEmail(String email, String text) {
		
			Properties props = new Properties();
			
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.socketFactory.class",
					"javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", "465");
			
			try {
				
			Properties p = new Properties();
			FileReader reader = new FileReader("configuration.properties");
			p.load(reader);
			
			
			
			
			
			Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
				
					protected PasswordAuthentication getPasswordAuthentication() {
							System.out.println(p.getProperty("USERNAME"));
					        return new PasswordAuthentication(p.getProperty("USERNAME"),"jusijusi");
					}
				});

			
				
				Message message = new MimeMessage(session);
				// configurak nacitaj mail
				message.setFrom(new InternetAddress("vava.brain.challenge@gmail.com"));
				message.setRecipients(Message.RecipientType.TO,
						InternetAddress.parse(email));
				message.setSubject("Brain challenge app");
				text = "Thank you for your play\nThank you for your game:\n\n";
				message.setText(text);
				

				Transport.send(message);
				
			} catch (IOException e) {
				
				e.printStackTrace();
				
			}

			 catch (MessagingException e) {
				throw new RuntimeException(e);
			}
		}
	
}
