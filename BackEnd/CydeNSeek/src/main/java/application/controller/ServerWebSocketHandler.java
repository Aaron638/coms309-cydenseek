package application.controller;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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

	public static final Map<Session, GameUser> gameusers = new HashMap<>();
	public static final Map<String, Game> games = new HashMap<>();

	@OnOpen
	public void onOpen(final Session session, @PathParam("gameSession") final String gameSession, @PathParam("username") final String username) {
		if(!userDB.findUserByUsername(username).isPresent()) {
			sendError(session, "User not found.");
			return;
		}
		if(!games.containsKey(gameSession)) {
			sendError(session, "Game not found.");
			return;
		}
		if(gameusers.values().stream().anyMatch(g -> g.getUsername().equals(username))) {
			sendError(session, "User already in game.");
			return;
		}
		if(gameusers.values().stream().filter(g -> g.getGameSession().equals(gameSession)).count() >= games.get(gameSession).getMaxplayers()) {
			sendError(session, "Game is full.");
			return;
		}
		LOG.info(username + " has connected.");
		GameUser gu = new GameUser();
		gu.setVerified(false);
		gu.setGameSession(gameSession);
		gu.setFound(false);
		gu.setUsername(username);
		final String userSession = Integer.toString((int)Math.floor(Math.random()*9000) + 1000);
		gu.setUserSession(userSession);
		gameusers.put(session, gu);
	}

	@OnMessage
	public void onMessage(final Session session, final String message) {
		final GameUser gu = gameusers.get(session);
		if(gu == null) {
			sendError(session, "Socket connection failed.");
			return;
		}
		final String username = gu.getUsername();
		final JSONObject msg;
		try {
			msg = new JSONObject(message);
		} catch(Exception e) {
			sendError(session, e.getMessage());
			LOG.error(e);
			return;
		}
		final String gameSession = gu.getGameSession();
		final Game game = games.get(gameSession);
		final Map<Session, GameUser> gameUsers = gameusers.entrySet().stream().filter(x -> gameSession.equals(x.getValue().getGameSession())).collect(Collectors.toMap(e->e.getKey(),e->e.getValue()));;
		if(!gu.isVerified().booleanValue()) {
			if(!msg.has("session")) {
				sendError(session, "Session token not present.");
				return;
			}
			if(!generalDB.findById(userDB.findUserByUsername(username).get().getGeneralId()).get().getSession().equals(msg.getString("session"))) {
				sendError(session, "Invalid session token.");
				return;
			}
			gu.setHider(gameUsers.values().stream().filter(x -> x.isHider() != null && x.isHider().booleanValue()).count() <= gameUsers.values().stream().filter(x -> x.isHider() != null && !x.isHider().booleanValue()).count());
			send(session, "{\"hider\":" + gu.isHider() + ",\"session\":\"" + gu.getUserSession() + "\"}");
			gu.setVerified(true);
		}
		if(!msg.has("latitude")) {
			sendError(session, "Latitude not present.");
			return;
		}
		if(!msg.has("longitude")) {
			sendError(session, "Longitude not present.");
			return;
		}
		if(!(msg.get("latitude") instanceof Double)) {
			sendError(session, "Latitude must be double.");
			return;
		}
		if(!(msg.get("longitude") instanceof Double)) {
			sendError(session, "Longitude must be double.");
			return;
		}
		final Stats stats = statsDB.findById(generalDB.findById(userDB.findUserByUsername(username).get().getGeneralId()).get().getStatsId()).get();
		final Double latitude = msg.getDouble("latitude");
		final Double longitude = msg.getDouble("longitude");
		if(gu.getLatitude() != null && gu.getLongitude() != null) stats.setTotDistance(stats.getTotDistance() + Math.sqrt(Math.pow(latitude - gu.getLatitude(), 2) + Math.pow(longitude - gu.getLongitude(), 2)));
		statsDB.saveAndFlush(stats);
		gu.setLatitude(latitude);
		gu.setLongitude(longitude);
		if(LocalTime.now().isBefore(game.getStartTime())) {
			send(session, "{\"timeLeft\":" + LocalTime.now().until(game.getStartTime(), ChronoUnit.MINUTES)+"}");
			return;
		}
		send(session, "{\"hiders\":" + gameUsers.values().stream().map(x -> {
			JSONObject j = new JSONObject();
			j.put("username", x.getUsername());
			j.put("latitude", x.getLatitude() + Math.random()*.001 -.0005);
			j.put("longitude", x.getLongitude() + Math.random()*.001 - .0005);
			return j;
		}).collect(Collectors.toList()).toString() + "}");
		if(!gu.isHider().booleanValue()) {
			if(msg.has("userSession")) {
				final Optional<Map.Entry<Session, GameUser>> found = gameUsers.entrySet().stream().filter(x -> x.getValue().getUserSession().equals(msg.getString("userSession"))).findFirst();
				if(!found.isPresent()) {
					sendError(session, "User not found.");
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
		final Map<Session, GameUser> usersLeft = gameUsers.entrySet().stream().filter(x -> x.getValue().isHider().booleanValue() && !x.getValue().isFound().booleanValue()).collect(Collectors.toMap(e->e.getKey(), e->e.getValue()));
		final long playersleft = usersLeft.size();
		if(playersleft == 0 || LocalTime.now().isAfter(game.getStartTime().plusMinutes(game.getDuration()))) {
			if(playersleft == 0) {
				gameUsers.values().stream().forEach(x -> {
					if(x.isHider().booleanValue()) return;
					final Stats s = statsDB.findById(generalDB.findById(userDB.findUserByUsername(x.getUsername()).get().getGeneralId()).get().getStatsId()).get();
					s.setGWSeeker(s.getGWSeeker() + 1);
					statsDB.saveAndFlush(s);
				});
				broadcast("{\"winner\":false}", gu.getGameSession());
			} else {
				usersLeft.values().stream().forEach(x -> {
					final Stats s = statsDB.findById(generalDB.findById(userDB.findUserByUsername(x.getUsername()).get().getGeneralId()).get().getStatsId()).get();
					s.setGWHider(s.getGWHider() + 1);
					statsDB.saveAndFlush(s);
				});
				broadcast("{\"winner\": true}", gu.getGameSession());
			}
			gameUsers.values().stream().forEach(x -> {
				final Stats s = statsDB.findById(generalDB.findById(userDB.findUserByUsername(x.getUsername()).get().getGeneralId()).get().getStatsId()).get();
				if(x.isHider().booleanValue()) s.setGPHider(s.getGPHider() + 1);
				else s.setGPSeeker(s.getGPSeeker() + 1);
				s.setTotTime((int)(s.getTotTime() + game.getGperiod() + (LocalTime.now().isBefore(game.getStartTime().plusMinutes(game.getDuration())) ? game.getStartTime().until(LocalTime.now(),ChronoUnit.MINUTES) : game.getDuration())));
				statsDB.saveAndFlush(s);
			});
			for(final Session s : gameUsers.keySet()) gameusers.remove(s);
			games.remove(gameSession);
			return;
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

	private static void sendError(final Session session, final String message) {
		send(session, "{\"error\":true,\"message\":\"" + message + "\"}");
	}

	private static void broadcast(final String message, final String gameSession) {
		gameusers.forEach((session, gameuser) -> {
			if(gameuser.getGameSession().equals(gameSession)) synchronized(session) {
				send(session, message);
			}
		});
	}
}