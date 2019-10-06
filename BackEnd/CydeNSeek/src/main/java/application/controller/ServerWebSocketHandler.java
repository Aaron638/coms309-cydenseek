package application.controller;

import java.util.HashMap;
import java.util.Map;

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
import application.db.UserDB;
import application.model.User;

@ServerEndpoint(value = "/location/{username}", configurator = CustomConfigurator.class)
@Component
public class ServerWebSocketHandler {

	private static final Log LOG = LogFactory.getLog(ServerWebSocketHandler.class);

	@Autowired
	private UserDB userDB;

	private static Map<String, Session> sessions = new HashMap<>();
	private static Map<Session, String> usernames = new HashMap<>();

	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username) {
		if(userDB.findUserByUsername(username) == null) {
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
		if(!userDB.findUserByUsername(username).getSession().equals(msg.getString("session"))) {
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
		User user = userDB.findUserByUsername(username);
		user.setLatitude(msg.getString("latitude"));
		user.setLongitude(msg.getString("longitude"));
		userDB.saveAndFlush(user);
		LOG.info(username + " has been updated.");
		JSONObject out = new JSONObject();
		out.put("username", username);
		out.put("latitude", msg.getString("latitude"));
		out.put("longitude", msg.getString("longitude"));
		broadcast(out.toString());
	}

	@OnClose
	public void onClose(Session session) {
		String username = usernames.get(session);
		if(username == null) return;
		sessions.remove(username);
		LOG.info(username + " has closed connection.");
		usernames.remove(session);
		broadcast("{\"username\":\"" + username + "\"}");
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

	private static void broadcast(String message) {
		sessions.forEach((username, session) -> {
			synchronized(session) {
				send(session, message);
			}
		});
	}
}