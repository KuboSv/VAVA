package com.server;

import java.sql.*;
import java.util.*;
import javax.ejb.*;
import javax.naming.*;
import javax.sql.DataSource;

import com.server.entity.*;


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
	public void pridaj(String meno, int body) {

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

			PreparedStatement stmt = conn.prepareStatement("insert into rebricek (meno, body) values ('"+meno+"',"+body+");");
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
}
