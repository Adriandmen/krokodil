package it;

import nl.adrianmensing.krokodil.KrokodilApplication;
import nl.adrianmensing.krokodil.database.service.dynamodb.DynamoDBService;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import javax.servlet.http.Cookie;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = KrokodilApplication.class)
public class PlayerControllerTest extends IntegrationTest {

    @BeforeAll
    public static void setup() throws InterruptedException {
        DynamoDBService.setupTables();
    }

    @Test
    public void BasicPlayerGenerationOnVisitTest() throws Exception {
        ResultActions result = mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));

        Cookie session_id = result.andReturn().getResponse().getCookie("session_id");

        assertThat(session_id).isNotNull();
        assertThat(session_id.getValue().trim()).matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }

    @Test
    public void GetPlayerInfoTest() throws Exception {
        Cookie session_id = generateNewPlayerCookie();
        ResultActions result = mvc.perform(get("/api/player/info").cookie(session_id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        String content = result.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject object = new JSONObject(content);

        assertThat(object.getJSONObject("body").getString("id")).isEqualTo(session_id.getValue());
        assertThat(object.getJSONObject("body").getString("username")).isEqualTo("");
        assertThat(object.getJSONObject("body").getString("game")).isEqualTo("");
    }

    @Test
    public void SetUsernameForPlayer() throws Exception {
        Cookie session_id = generateNewPlayerCookie();
        mvc.perform(put("/api/player/username").cookie(session_id).param("username", "Adnan"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        String content = mvc.perform(post("/api/player/info").param("user_id", session_id.getValue()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        JSONObject object = new JSONObject(content);

        assertThat(object.getString("id")).isEqualTo(session_id.getValue());
        assertThat(object.getString("username")).isEqualTo("Adnan");
    }

    @Test
    public void CreateNewGameTest() throws Exception {
        Cookie session_id = generateNewPlayerCookie();

        mvc.perform(post("/api/game/create").cookie(session_id))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        mvc.perform(put("/api/player/username").cookie(session_id).param("username", "Adnan"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        String content = mvc.perform(post("/api/game/create").cookie(session_id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        JSONObject playerResponse = new JSONObject(content);
        String gameID = playerResponse.getString("game");

        assertThat(gameID).matches("^[A-Z]{6}$");

        String gameContent = mvc.perform(get("/api/game").cookie(session_id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        JSONObject gameObject = new JSONObject(gameContent);

        assertThat(gameObject.getString("id")).isEqualTo(gameID);
        assertThat(gameObject.getJSONArray("players").length()).isEqualTo(1);
        assertThat(gameObject.getJSONArray("players").getString(0)).isEqualTo(session_id.getValue());
        assertThat(gameObject.getJSONObject("host").getString("id")).isEqualTo(session_id.getValue());
        assertThat(gameObject.getString("state")).isEqualTo("INITIALIZING");
        assertThat(gameObject.getJSONObject("settings").getJSONObject("settings").getInt("teeth_count")).isEqualTo(10);
        assertThat(gameObject.getJSONObject("settings").getJSONObject("settings").getInt("bad_teeth_count")).isEqualTo(1);
        assertThat(gameObject.getJSONObject("position").length()).isEqualTo(0);
        assertThat(gameObject.has("salt")).isFalse();
    }

    @Test
    public void CreateAndStartNewGameTest() throws Exception {
        Cookie player1 = generateNewPlayerCookie("Adnan");
        Cookie player2 = generateNewPlayerCookie("Bariuw");
        Cookie player3 = generateNewPlayerCookie("Carol");
        Cookie player4 = generateNewPlayerCookie("Dirk");
        Cookie player5 = generateNewPlayerCookie("Emma");

        String hostContent = mvc.perform(post("/api/game/create").cookie(player1))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        JSONObject hostObject = new JSONObject(hostContent);
        String gameID = hostObject.getString("game");

        mvc.perform(post("/api/game/join").cookie(player2).param("game_id", gameID)).andExpect(status().isOk());
        mvc.perform(post("/api/game/join").cookie(player3).param("game_id", gameID)).andExpect(status().isOk());
        mvc.perform(post("/api/game/join").cookie(player4).param("game_id", gameID)).andExpect(status().isOk());
        mvc.perform(post("/api/game/join").cookie(player5).param("game_id", gameID)).andExpect(status().isOk());

        String gameContent = mvc.perform(get("/api/game").cookie(player3))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        JSONObject gameObject = new JSONObject(gameContent);
        assertThat(gameObject.getString("state")).isEqualTo("INITIALIZING");

        mvc.perform(post("/api/game/action").cookie(player2).param("action_name", "START_GAME"))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/game/action").cookie(player1).param("action_name", "undefined action"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/game/action").cookie(player1).param("action_name", "UPDATE_SETTING").param("params", "{\"key\": \"teeth_count\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/game/action").cookie(player1).param("action_name", "UPDATE_SETTING").param("params", "{\"value\": 3}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/game/action").cookie(player1).param("action_name", "UPDATE_SETTING").param("params", "{\"key\": \"teeth_count\", \"value\": 16}"))
                .andExpect(status().isOk());
        mvc.perform(post("/api/game/action").cookie(player1).param("action_name", "START_GAME"))
                .andExpect(status().isOk());

        String updatedGameObject = mvc.perform(get("/api/game").cookie(player4))
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        gameObject = new JSONObject(updatedGameObject);

        assertThat(gameObject.getJSONArray("players").length()).isEqualTo(5);
        assertThat(gameObject.getJSONObject("position").getJSONArray("TeethList").length()).isEqualTo(16);
        assertThat(gameObject.getJSONObject("position").getString("BadTooth")).matches("^[0-9a-f]{32}$");
    }
}
