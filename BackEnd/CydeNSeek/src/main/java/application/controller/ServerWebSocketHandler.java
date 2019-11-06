package application.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
import application.db.GeneralDB;
import application.db.UserDB;
import application.model.GameUser;

@ServerEndpoint(value = "/user/{username}/location", configurator = CustomConfigurator.class)
@Component
public class ServerWebSocketHandler {

	private static final Log LOG = LogFactory.getLog(ServerWebSocketHandler.class);

	@Autowired
	private GeneralDB generalDB;

	@Autowired
	private UserDB userDB;

	private static Map<String, Session> sessions = new HashMap<>();
	private static Map<Session, String> usernames = new HashMap<>();
	public static Map<String, GameUser> gameusers = new HashMap<>();

	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username) {
		if(!userDB.findUserByUsername(username).isPresent()) {
			send(session, "{\"error\":true,\"message\":\"User not found.\"}");
			return;
		}
		LOG.info(username + " has connected.");
		sessions.put(username, session);
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
		if(!generalDB.findById(userDB.findUserByUsername(username).get().getGeneralId()).get().getSession().equals(msg.getString("session"))) {
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
		GameUser gu = gameusers.get(username);
		if(gu == null) {
			if(!msg.has("gameSession")) {
				send(session, "{}");
				return;
			}
			if(!msg.has("hider")) {
				send(session, "{}");
				return;
			}
			if(!(msg.get("hider") instanceof Boolean)) {
				send(session, "{}");
				return;
			}
			gu = new GameUser();
			gu.setFound(false);
			String userSession = UUID.randomUUID().toString();
			gu.setUserSession(userSession);
			gu.setGameSession(msg.getString("gameSession"));
			gu.setHider(msg.getBoolean("hider"));
		}
		gu.setLatitude(msg.getDouble("latitude"));
		gu.setLongitude(msg.getDouble("longitude"));
		final String gameSession = gu.getGameSession();
		final GameUser me = gu;
		Map<String, GameUser> gameUsers = gameusers.entrySet().stream().filter(x -> gameSession.equals(x.getValue().getGameSession())).collect(Collectors.toMap(e->e.getKey(),e->e.getValue()));;
		gameUsers.values().stream().forEach(x -> {
			if(me.getHider().booleanValue() == x.getHider().booleanValue()) return;
			if(Math.abs(me.getLatitude().doubleValue() - x.getLatitude().doubleValue()) < 1.5 && Math.abs(me.getLongitude().doubleValue() - x.getLongitude().doubleValue()) < 1.5) {
				if(me.getHider().booleanValue()) {
					me.setFound(true);
					send(sessions.get(username), "{\"found\":true}");
				} else {
					String gusername = gameUsers.entrySet().stream().filter(y -> y.getValue() == x).findFirst().get().getKey();
					gameusers.get(gusername).setFound(true);
					send(sessions.get(gusername), "{\"found\":true}");
				}
			}
		});
		Map<String, GameUser> usersLeft = gameUsers.entrySet().stream().filter(x -> x.getValue().getHider().booleanValue() && !x.getValue().getFound().booleanValue()).collect(Collectors.toMap(e->e.getKey(), e->e.getValue()));
		long playersleft = usersLeft.size();
		if(playersleft <= 1) {
			if(playersleft == 0) broadcast("{\"winner\":false}", gameusers.get(username).getGameSession());
			else broadcast("{\"winner\":\"" + usersLeft.entrySet().stream().findFirst().get().getKey() + "\"}", gu.getGameSession());
			sessions.clear();
			usernames.clear();
			return;
		}
		LOG.info(username + " has been updated.");
		JSONObject out = new JSONObject();
		out.put("username", username);
		out.put("latitude", msg.getDouble("latitude"));
		out.put("longitude", msg.getDouble("longitude"));
		broadcast(out.toString(), gameSession);
	}

	@OnClose
	public void onClose(Session session) {
		String username = usernames.get(session);
		if(username == null) return;
		sessions.remove(username);
		LOG.info(username + " has closed connection.");
		usernames.remove(session);
		broadcast("{\"username\":\"" + username + "\"}", gameusers.remove(username).getGameSession());
	}

	@OnError
	public void onError(Session session, Throwable e) {
		LOG.error(e);
		String username = usernames.get(session);
		usernames.remove(session);
		sessions.remove(username);
		gameusers.remove(username);
	}

	private static void send(Session session, String message) {
		try {
			session.getBasicRemote().sendText(message);
		} catch(Exception e) {
			LOG.error(e);
		}
	}

	private static void broadcast(String message, String gameId) {
		sessions.forEach((username, session) -> {
			if(gameusers.get(username).getGameSession().equals(gameId)) synchronized(session) {
				send(session, message);
			}
		});
	}
}