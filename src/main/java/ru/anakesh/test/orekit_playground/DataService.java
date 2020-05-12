package ru.anakesh.test.orekit_playground;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import ru.anakesh.test.orekit_playground.data.SatelliteOrbitData;

import java.io.File;
import java.io.IOException;

@Slf4j
public class DataService {

    @NotNull
    @SneakyThrows
    public SatelliteOrbitData parseSatelliteOrbitDataFromFIle(@NotNull File file) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(file, SatelliteOrbitData.class);
        } catch (IOException e) {
            log.error("Unexpected error while parsing satellite orbit data json", e);
            throw e;
        }
    }
}
