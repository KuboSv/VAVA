package com.server;

import java.util.List;
import javax.ejb.Remote;

import com.server.entity.*;

@Remote
public interface ServerBeanRemote {
	
	public List<Question> getOtazky(int kategoria_id);
	
	void pridaj(String meno,int body);
	
	public List<TopPlayers> getTopPlayers();

}
