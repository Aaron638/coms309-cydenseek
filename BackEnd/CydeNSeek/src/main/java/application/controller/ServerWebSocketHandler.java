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
import application.db.GameDB;
import application.db.GeneralDB;
import application.db.UserDB;
import application.model.GameUser;

@ServerEndpoint(value = "/{gameSession}/{username}", configurator = CustomConfigurator.class)
@Component
public class ServerWebSocketHandler {

	private static final Log LOG = LogFactory.getLog(ServerWebSocketHandler.class);

	@Autowired
	private GeneralDB generalDB;

	@Autowired
	private UserDB userDB;

	@Autowired
	private GameDB gameDB;

	public static Map<Session, GameUser> gameusers = new HashMap<>();

	@OnOpen

	public void onOpen(final Session session, @PathParam("gameSession") final String gSession, @PathParam("username") final String username) {
		final UUID gameSession = UUID.fromString(gSession);
		if(!userDB.findUserByUsername(username).isPresent()) {
			send(session, "{\"error\":true,\"message\":\"User not found.\"}");
			return;
		}
		if(!gameDB.findGameBySession(gameSession).isPresent()) {
			send(session, "{\"error\":true,\"message\":\"Game not found.\"}");
			return;
		}
		LOG.info(username + " has connected.");
		GameUser gu = new GameUser();
		gu.setVerified(false);
		gu.setGameSession(gameSession);
		gu.setFound(false);
		gu.setUsername(username);
		final String userSession = UUID.randomUUID().toString();
		gu.setUserSession(userSession);
		gameusers.put(session, gu);
	}

	@OnMessage
	public void onMessage(final Session session, final String message) {
		final GameUser gu = gameusers.get(session);
		if(gu == null) {
			send(session, "{\"error\":true,\"message\":\"Socket connection failed.\"}");
			return;
		}
		final String username = gu.getUsername();
		final JSONObject msg;
		try {
			msg = new JSONObject(message);
		} catch(Exception e) {
			send(session, "{\"error\":true,\"message\":\"" + e.getMessage() + "\"}");
			LOG.error(e);
			return;
		}
		if(!gu.getVerified().booleanValue()) {
			if(!msg.has("session")) {
				send(session, "{\"error\":true,\"message\":\"Session token not present.\"}");
				return;
			}
			if(!generalDB.findById(userDB.findUserByUsername(username).get().getGeneralId()).get().getSession().equals(msg.getString("session"))) {
				send(session, "{\"error\":true,\"message\":\"Invalid session token.\"}");
				return;
			}
			if(!msg.has("hider")) {
				send(session, "{\"error\":true,\"message\":\"Must specify if hider or not.\"}");
				return;
			}
			if(!(msg.get("hider") instanceof Boolean)) {
				send(session, "{\"error\":true,\"message\":\"Hider must be boolean.\"}");
				return;
			}
			gu.setHider(msg.getBoolean("hider"));
			gu.setVerified(true);
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
		gu.setLatitude(msg.getDouble("latitude"));
		gu.setLongitude(msg.getDouble("longitude"));

		final UUID gameSession = gu.getGameSession();

		final Map<Session, GameUser> gameUsers = gameusers.entrySet().stream().filter(x -> gameSession.equals(x.getValue().getGameSession())).collect(Collectors.toMap(e->e.getKey(),e->e.getValue()));;
		gameUsers.entrySet().stream().forEach(x -> {
			final GameUser other = x.getValue();
			if(gu.getHider().booleanValue() == other.getHider().booleanValue()) return;
			if(Math.abs(gu.getLatitude().doubleValue() - other.getLatitude().doubleValue()) < 1.5 && Math.abs(gu.getLongitude().doubleValue() - other.getLongitude().doubleValue()) < 1.5) {
				if(gu.getHider().booleanValue()) {
					gu.setFound(true);
					send(session, "{\"found\":true}");
				} else {
					gameusers.get(x.getKey()).setFound(true);
					send(x.getKey(), "{\"found\":true}");
				}
			}
		});
		final Map<Session, GameUser> usersLeft = gameUsers.entrySet().stream().filter(x -> x.getValue().getHider().booleanValue() && !x.getValue().getFound().booleanValue()).collect(Collectors.toMap(e->e.getKey(), e->e.getValue()));
		final long playersleft = usersLeft.size();
		if(playersleft <= 1) {
			if(playersleft == 0) broadcast("{\"winner\":false}", gu.getGameSession());
			else broadcast("{\"winner\":\"" + usersLeft.entrySet().stream().findFirst().get().getKey() + "\"}", gu.getGameSession());
			return;
		}
		LOG.info(username + " has been updated.");
		final JSONObject out = new JSONObject();
		out.put("username", username);
		out.put("latitude", msg.getDouble("latitude"));
		out.put("longitude", msg.getDouble("longitude"));
		broadcast(out.toString(), gameSession);
	}

	@OnClose
	public void onClose(final Session session) {
		final GameUser gu = gameusers.remove(session);
		LOG.info(gu.getUsername() + " has closed connection.");
		broadcast("{\"username\":\"" + gu.getUsername() + "\"}", gu.getGameSession());
	}

	@OnError
	public void onError(final Session session, final Throwable e) {
		LOG.error(e);
		gameusers.remove(session);
	}

	private static void send(final Session session, final String message) {
		try {
			session.getBasicRemote().sendText(message);
		} catch(Exception e) {
			LOG.error(e);
		}
	}


	private static void broadcast(final String message, final UUID gameSession) {
		gameusers.forEach((session, gameuser) -> {
			if(gameuser.getGameSession().compareTo(gameSession) == 0) synchronized(session) {
				send(session, message);
			}
		});
	}
}
