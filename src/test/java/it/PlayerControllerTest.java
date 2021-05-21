package it;

import nl.adrianmensing.krokodil.KrokodilApplication;
import nl.adrianmensing.krokodil.database.service.dynamodb.DynamoDBService;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.servlet.http.Cookie;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KrokodilApplication.class)
@AutoConfigureMockMvc
public class PlayerControllerTest {

    @BeforeAll
    public static void setup() throws InterruptedException {
        DynamoDBService.setupTables();
    }

    @Autowired
    private MockMvc mvc;

    @Test
    public void BasicPlayerGenerationOnVisit() throws Exception {
        ResultActions result = mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML));

        Cookie session_id = result.andReturn().getResponse().getCookie("session_id");

        assertThat(session_id).isNotNull();
        assertThat(session_id.getValue().trim()).matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }

    @Test
    public void GetPlayerInfo() throws Exception {
        Cookie session_id = generateNewPlayerCookie();
        ResultActions result = mvc.perform(get("/api/player/info").cookie(session_id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        String content = result.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        JSONObject object = new JSONObject(content);

        assertThat(object.getJSONObject("body").getString("id")).isEqualTo(session_id.getValue());
        assertThat(object.getJSONObject("body").getString("username")).isEqualTo("");
    }

    private Cookie generateNewPlayerCookie() throws Exception {
        return mvc.perform(get("/"))
                .andReturn()
                .getResponse()
                .getCookie("session_id");
    }
}
