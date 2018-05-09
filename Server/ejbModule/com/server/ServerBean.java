package com.server;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.*;
import javax.naming.*;
import javax.sql.DataSource;
import java.io.IOException;
import javax.mail.*;
import javax.mail.internet.*;

import com.server.entity.*;

/**
 * @author Jakub Juško, Ivan Petrov
 */

@Stateless
@LocalBean
@Remote(ServerBeanRemote.class)
public class ServerBean implements ServerBeanRemote {

	
	private final static Logger LOG = Logger.getLogger(ServerBean.class.getName());
	
	/**
	 * Metoda ktora nacita z databazy otazky a odpovede podla kategoria
	 * @param	kategoria_id predstavuje index kategorie
	 * @return	zoznam otazok a odpovedi 
	 * @see		Question
	 */
	public List<Question> getOtazky(int kategoria_id) {
		
		List<Question> otazky = new ArrayList<Question>();

		Properties p = new Properties();
		Connection conn = null;
		
		try {
		
			p.load(this.getClass().getResourceAsStream("/configuration.properties"));	
			Context ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup(p.getProperty("DATASOURCE"));
			conn = (Connection) ds.getConnection();

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
			}

			stmt.close();
			conn.close();

		} catch (IOException | NamingException | SQLException e) {

			LOG.log(Level.INFO, "Loggujem vyminku na serveri( metoda: getOtazky): ", e);
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
		
		Properties p = new Properties();
		Connection conn = null;
		
		try {
				
			Context ctx = new InitialContext();
			p.load(this.getClass().getResourceAsStream("/configuration.properties"));	
			DataSource ds = (DataSource) ctx.lookup(p.getProperty("DATASOURCE"));
			conn = (Connection) ds.getConnection();
	
			PreparedStatement stmt = conn.prepareStatement("insert into rebricek (meno, body) values ('"+hrac.getName()+"',"+hrac.getScore()+");");
			stmt.executeUpdate();
			
			stmt.close();
			conn.close();

		} catch (IOException | NamingException | SQLException e) {

			LOG.log(Level.INFO, "Loggujem vyminku na serveri( metoda: pridaj): ", e);
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
		PreparedStatement stmt= null;
		Properties p = new Properties();
		
		try {
		
			Context ctx = new InitialContext();
			p.load(this.getClass().getResourceAsStream("/configuration.properties"));
			DataSource ds = (DataSource) ctx.lookup(p.getProperty("DATASOURCE"));
			conn = (Connection) ds.getConnection();

			stmt = conn.prepareStatement(p.getProperty("SELECTHRACI"));
			ResultSet rs = stmt.executeQuery();
	
			while (rs.next()) {
				
				TopPlayers top = new TopPlayers();
				
				top.setName(rs.getString(1));
				top.setScore(rs.getInt(2));
				hraci.add(top);
			}
			
			

		} catch (IOException | NamingException | SQLException e) {
			
			LOG.log(Level.INFO, "Loggujem vyminku na serveri( metoda: getTopPlayers): ", e);

		} finally {

			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {

				LOG.log(Level.INFO, "Loggujem vyminku na serveri( metoda: getTopPlayers): ", e);
			}

		}

		return hraci;
	}

	/**
	 * 
	 * Metoda ktora posli na zadanu emailovu adresu email s menom hraca 
	 * a poctom ziskanych bodov
	 * @param	email	cielova emailova adresa
	 * @param	hrac 	aktualny hrac 
	 */
	public void SentEmail(String email, TopPlayers hrac) {

		try {	
			
			Properties props = new Properties();
			Properties p = new Properties();
			
			p.load(this.getClass().getResourceAsStream("/configuration.properties"));
			props.load(this.getClass().getResourceAsStream("/mail.properties"));

			Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {

				protected PasswordAuthentication getPasswordAuthentication() {

					return new PasswordAuthentication(p.getProperty("USERNAME"), p.getProperty("PASSWORD"));
				}
			});

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(p.getProperty("EMAIL")));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
			message.setSubject(p.getProperty("SUBJECT"));
			
			String text = "Dakujeme "+hrac.getName()+", ze ste si vyskusali svoje znalosti "
					+ " pomocou nasej aplikacie Brain Challenge\nVase ziskane skore je "+hrac.getScore()+"\n\n\n\n"
							+ "Brain Challenge Development Team,\nVasVa 2018 FIIT STU";
			message.setText(text);

			Transport.send(message);


		} catch (IOException | MessagingException e) {
			
			LOG.log(Level.SEVERE, "Loggujem vyminku na serveri( metoda: sendMail): ", e);
		}
	}
}