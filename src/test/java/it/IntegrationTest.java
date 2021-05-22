package it;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@AutoConfigureMockMvc
public abstract class IntegrationTest {

    @Autowired
    public MockMvc mvc;

    Cookie generateNewPlayerCookie() throws Exception {
        return mvc.perform(get("/"))
                .andReturn()
                .getResponse()
                .getCookie("session_id");
    }

    Cookie generateNewPlayerCookie(String username) throws Exception {
        Cookie session_id = generateNewPlayerCookie();
        mvc.perform(post("/api/player/username").cookie(session_id).param("username", username));
        return session_id;
    }
}
