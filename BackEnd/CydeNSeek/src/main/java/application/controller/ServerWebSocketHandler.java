package application.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import application.config.CustomConfigurator;
import application.db.GameUserDB;
import application.db.GeneralDB;
import application.db.UserDB;
import application.model.GameUser;
import application.model.User;

@ServerEndpoint(value = "/user/{username}/location", configurator = CustomConfigurator.class)
@Component
public class ServerWebSocketHandler {

	private static final Log LOG = LogFactory.getLog(ServerWebSocketHandler.class);

	@Autowired
	private UserDB userDB;
	
	@Autowired
	private GameUserDB guDB;
	
	@Autowired
	private GeneralDB genDB;
	

	private static Map<String, Session> sessions = new HashMap<>();
	private static Map<String, Integer> games = new HashMap<>();
	private static Map<Session, String> usernames = new HashMap<>();

	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username) {
		if(!userDB.findUserByUsername(username).isPresent()) {
			send(session, "{\"error\":true,\"message\":\"User not found.\"}");
			return;
		}
		LOG.info(username + " has connected.");
		sessions.put(username, session);
		games.put(username, userDB.findUserByUsername(username).get().getGen().getGameUser().getGame().getGameId());
		usernames.put(session, username);
	}

	@OnMessage
	public void onMessage(Session session, String message) {
		String username = usernames.get(session);
		if(username == null) {
			send(session, "{\"error\":true,\"message\":\"Socket connection failed.\"}");
			return;
		}
		JSONObject msg;
		try {
			msg = new JSONObject(message);
		} catch(Exception e) {
			send(session, "{\"error\":true,\"message\":\"" + e.getMessage() + "\"}");
			LOG.error(e);
			return;
		}
		if(!msg.has("session")) {
			send(session, "{\"error\":true,\"message\":\"Session token not present.\"}");
			return;
		}
		if(!userDB.findUserByUsername(username).get().getGen().getSession().equals(msg.getString("session"))) {
			send(session, "{\"error\":true,\"message\":\"Invalid session token.\"}");
			return;
		}
		if(!msg.has("latitude")) {
			send(session, "{\"error\":true,\"message\":\"Latitude not present.\"}");
			return;
		}
		if(!msg.has("longitude")) {
			send(session, "{\"error\":true,\"message\":\"Longitude not present.\"}");
			return;
		}
		if(!(msg.get("latitude") instanceof Double)) {
			send(session, "{\"error\":true,\"message\":\"Latitude must be double.\"}");
			return;
		}
		if(!(msg.get("longitude") instanceof Double)) {
			send(session, "{\"error\":true,\"message\":\"Longitude must be double.\"}");
			return;
		}
		Optional<User> user = userDB.findUserByUsername(username);
		GameUser gu = user.get().getGen().getGameUser();
		gu.setLatitude(msg.getDouble("latitude"));
		gu.setLongitude(msg.getDouble("longitude"));
		guDB.saveAndFlush(gu);
		genDB.findUsersByGame(user.get().getGen().getGameUser().getGame().getGameId(), (x,y) -> 0).stream().forEach(x -> {
			if(gu.getIsHider().booleanValue() == x.getGameUser().getIsHider().booleanValue()) return;
			if(Math.abs(gu.getLatitude().doubleValue() - x.getGameUser().getLatitude().doubleValue()) < 1.5 && Math.abs(gu.getLongitude().doubleValue() - x.getGameUser().getLongitude().doubleValue()) < 1.5) {
				if(gu.getIsHider().booleanValue()) {
					gu.setFound(true);
					send(sessions.get(username), "{\"found\":true}");
					guDB.saveAndFlush(gu);
				} else {
					GameUser g = x.getGameUser();
					g.setFound(true);
					send(sessions.get(x.getUser().getUsername()), "{\"found\":true}");
					guDB.saveAndFlush(g);
				}
			}
		});
		long playersleft = genDB.findUsersByGame(user.get().getGen().getGameUser().getGame().getGameId(), (x,y) -> 0).stream().filter(x -> x.getGameUser().getIsHider().booleanValue() && !x.getGameUser().getFound().booleanValue()).count();
		if(playersleft <= 1) {
			if(playersleft == 0) broadcast("{\"winner\":false}", user.get().getGen().getGameUser().getGame().getGameId());
			else broadcast("{\"winner\":\"" + genDB.findUsersByGame(user.get().getGen().getGameUser().getGame().getGameId(), (x,y) -> 0).stream().filter(x -> x.getGameUser().getIsHider().booleanValue() && !x.getGameUser().getFound().booleanValue()).findFirst().get().getUser().getUsername() + "\"}", gu.getGame().getGameId());
			sessions.clear();
			usernames.clear();
			return;
		}
		LOG.info(username + " has been updated.");
		JSONObject out = new JSONObject();
		out.put("username", username);
		out.put("latitude", msg.getDouble("latitude"));
		out.put("longitude", msg.getDouble("longitude"));
		broadcast(out.toString(), user.get().getGen().getGameUser().getGame().getGameId());
	}

	@OnClose
	public void onClose(Session session) {
		String username = usernames.get(session);
		if(username == null) return;
		sessions.remove(username);
		LOG.info(username + " has closed connection.");
		usernames.remove(session);
		broadcast("{\"username\":\"" + username + "\"}", games.get(username));
		games.remove(username);
	}

	@OnError
	public void onError(Session session, Throwable e) {
		LOG.error(e);
	}

	private static void send(Session session, String message) {
		try {
			session.getBasicRemote().sendText(message);
		} catch(Exception e) {
			LOG.error(e);
		}
	}

	private static void broadcast(String message, Integer gameId) {
		sessions.forEach((username, session) -> {
			if(games.get(username).equals(gameId)) synchronized(session) {
				send(session, message);
			}
		});
	}
}