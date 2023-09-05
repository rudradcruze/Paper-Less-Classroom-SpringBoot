package bd.edu.diu.cis.classroom.utils;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RandomString {
    public String getRandomId() {
        UUID randomUUID = UUID.randomUUID();
        return randomUUID.toString().replaceAll("_", "");
    }
}
