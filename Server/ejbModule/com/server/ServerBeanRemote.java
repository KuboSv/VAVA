package com.server;

import java.util.List;
import javax.ejb.Remote;
import com.server.entity.*;

/**
 * @author Jakub Juško, Ivan Petrov
 */
@Remote
public interface ServerBeanRemote {
	
	public List<Question> getOtazky(int kategoria_id);
	
	void pridaj(TopPlayers hrac);
	
	public List<TopPlayers> getTopPlayers();
	
	public void SentEmail(String email, TopPlayers hrac);

}
