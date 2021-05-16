package nl.adrianmensing.krokodil.controller.api;

import nl.adrianmensing.krokodil.logic.Player;
import nl.adrianmensing.krokodil.response.impl.JSONResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @GetMapping("/")
    public ResponseEntity<?> test() {
        Map<Integer, String> m = new HashMap<>();

        m.put(1, "A");
        m.put(2, "B");
        m.put(3, "C");

        return new JSONResponse<>(m).build();
    }

}
