package ru.anakesh.test.orekit_playground;

import org.junit.jupiter.api.Test;
import ru.anakesh.test.orekit_playground.data.SatelliteOrbitData;

import java.io.File;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataServiceTest {
    private final DataService dataService = new DataService();

    @Test
    void parseSatelliteOrbitDataFromFIle() {
        File testDataFile = new File(this.getClass().getClassLoader().getResource("file/test_data.json").getFile());
        int testDataNorad = 44797;
        LocalDateTime testDataDateTime = LocalDateTime.of(2020, 1, 1, 7, 35, 2);
        int testDataSize = 173;

        SatelliteOrbitData satelliteOrbitData = dataService.parseSatelliteOrbitDataFromFIle(testDataFile);
        LocalDateTime firstStateDateTime = satelliteOrbitData.getStates().get(0).getDateTime();

        assertEquals(satelliteOrbitData.getSatelliteInfo().getNorad(), testDataNorad);
        assertEquals(testDataDateTime, firstStateDateTime);
        assertEquals(satelliteOrbitData.getStates().size(), testDataSize);
    }
}