package application.controller;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collector;
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import application.config.CustomConfigurator;
import application.db.GameDB;
import application.db.GeneralDB;
import application.db.StatsDB;
import application.db.UserDB;
import application.model.Game;
import application.model.GameUser;
import application.model.Stats;

@ServerEndpoint(value = "/{gameSession}/{username}", configurator = CustomConfigurator.class)
@Component
public class ServerWebSocketHandler {

	private static final Log LOG = LogFactory.getLog(ServerWebSocketHandler.class);

	@Autowired
	private GeneralDB generalDB;

	@Autowired
	private UserDB userDB;

	@Autowired
	private StatsDB statsDB;

	@Autowired
	private GameDB gameDB;

	public static Map<Session, GameUser> gameusers = new HashMap<>();

	@OnOpen
	public void onOpen(final Session session, @PathParam("gameSession") final String gSession, @PathParam("username") final String username) {
		final UUID gameSession;
		try {
			gameSession = UUID.fromString(gSession);
		} catch(IllegalArgumentException e) {
			LOG.error(e);
			send(session, "{\"error\":true,\"message\":\"\"}");
			return;
		}
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
		final Game game = gameDB.findGameBySession(gu.getGameSession()).get();
		final String username = gu.getUsername();
		final JSONObject msg;
		try {
			msg = new JSONObject(message);
		} catch(Exception e) {
			send(session, "{\"error\":true,\"message\":\"" + e.getMessage() + "\"}");
			LOG.error(e);
			return;
		}
		final UUID gameSession = gu.getGameSession();
		final Map<Session, GameUser> gameUsers = gameusers.entrySet().stream().filter(x -> gameSession.equals(x.getValue().getGameSession())).collect(Collectors.toMap(e->e.getKey(),e->e.getValue()));;
		if(!gu.isVerified().booleanValue()) {
			if(!msg.has("session")) {
				send(session, "{\"error\":true,\"message\":\"Session token not present.\"}");
				return;
			}
			if(!generalDB.findById(userDB.findUserByUsername(username).get().getGeneralId()).get().getSession().equals(msg.getString("session"))) {
				send(session, "{\"error\":true,\"message\":\"Invalid session token.\"}");
				return;
			}
			gu.setHider(gameUsers.values().stream().filter(x -> x.isHider() != null && x.isHider().booleanValue()).count() <= gameUsers.values().stream().filter(x -> x.isHider() != null && !x.isHider().booleanValue()).count());
			send(session, "{\"hider\":" + gu.isHider() + ",\"session\":\"" + gu.getUserSession() + "\"}");
			gu.setVerified(true);
		}
		if(LocalTime.now().isBefore(game.getStartTime())) return;
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
		final Stats stats = statsDB.findById(generalDB.findById(userDB.findUserByUsername(username).get().getGeneralId()).get().getStatsId()).get();
		final Double latitude = msg.getDouble("latitude");
		final Double longitude = msg.getDouble("longitude");
		if(gu.getLatitude() != null && gu.getLongitude() != null) stats.setTotDistance(stats.getTotDistance() + Math.sqrt(Math.pow(latitude - gu.getLatitude(), 2) + Math.pow(longitude - gu.getLongitude(), 2)));
		statsDB.saveAndFlush(stats);
		gu.setLatitude(latitude);
		gu.setLongitude(longitude);
		if(!gu.isHider().booleanValue()) {
			if(msg.has("userSession")) {
				final Optional<Map.Entry<Session, GameUser>> found = gameUsers.entrySet().stream().filter(x -> x.getValue().getUserSession().equals(msg.getString("userSession"))).findFirst();
				if(!found.isPresent()) {
					send(session, "{\"error\":true,\"message\":\"User not found.\"}");
					return;
				}
				final Map.Entry<Session, GameUser> foundUser = found.get();
				foundUser.getValue().setFound(true);
				send(foundUser.getKey(), "{\"found\":true}");
				send(session, "{\"foundUser\":\"" + foundUser.getValue().getUsername() + "\"}");
			}
			send(session, "{\"seekers\":" + gameUsers.values().stream().filter(x -> !x.isHider().booleanValue()).map(x -> {
				final JSONObject obj = new JSONObject();
				obj.put("username", x.getUsername());
				obj.put("latitude", x.getLatitude());
				obj.put("longitude", x.getLongitude());
				return obj;
			}).collect(Collector.of(JSONArray::new,JSONArray::put,JSONArray::put)).toString() + "}");
		}
		/*
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
		*/
		if(LocalTime.now().isAfter(game.getStartTime().plusMinutes(game.getDuration()))) {
			final Map<Session, GameUser> usersLeft = gameUsers.entrySet().stream().filter(x -> x.getValue().isHider().booleanValue() && !x.getValue().isFound().booleanValue()).collect(Collectors.toMap(e->e.getKey(), e->e.getValue()));
			final long playersleft = usersLeft.size();
			if(playersleft <= 1) {
				if(playersleft == 0) {
					gameUsers.values().stream().forEach(x -> {
						if(x.isHider().booleanValue()) return;
						final Stats s = statsDB.findById(generalDB.findById(userDB.findUserByUsername(x.getUsername()).get().getGeneralId()).get().getStatsId()).get();
						s.setGWSeeker(s.getGWSeeker() + 1);
						statsDB.saveAndFlush(s);
					});
					broadcast("{\"winner\":false}", gu.getGameSession());
				} else {
					final String winner = usersLeft.entrySet().stream().findFirst().get().getValue().getUsername();
					final Stats s = statsDB.findById(generalDB.findById(userDB.findUserByUsername(winner).get().getGeneralId()).get().getStatsId()).get();
					s.setGWHider(s.getGWHider() + 1);
					statsDB.saveAndFlush(s);
					broadcast("{\"winner\":\"" + winner + "\"}", gu.getGameSession());
				}
				gameUsers.values().stream().forEach(x -> {
					final Stats s = statsDB.findById(generalDB.findById(userDB.findUserByUsername(x.getUsername()).get().getGeneralId()).get().getStatsId()).get();
					if(x.isHider().booleanValue()) s.setGPHider(s.getGPHider() + 1);
					else s.setGPSeeker(s.getGPSeeker() + 1);
					statsDB.saveAndFlush(s);
				});
				for(Session s : gameUsers.keySet()) gameusers.remove(s);
				gameDB.deleteById(gameSession);
				return;
			}
		}
		LOG.info(username + " has been updated.");
	}

	@OnClose
	public void onClose(final Session session) {
		final GameUser gu = gameusers.remove(session);
		if(gu == null) return;
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