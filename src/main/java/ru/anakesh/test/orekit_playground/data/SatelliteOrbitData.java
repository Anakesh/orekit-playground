package ru.anakesh.test.orekit_playground.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
@AllArgsConstructor
public class SatelliteOrbitData {
    private SatelliteInfo satelliteInfo;
    private Frame frame = FramesFactory.getTEME();
    private List<SatelliteState> states;

    public SatelliteOrbitData(SatelliteInfo satelliteInfo, List<SatelliteState> states) {
        this.satelliteInfo = satelliteInfo;
        this.states = states;
    }


    @JsonCreator
    public SatelliteOrbitData(
            @JsonProperty("norad") int norad,
            @JsonProperty("startDay") int startDay,
            @JsonProperty("startSecond") double startSecond,
            @JsonProperty("stateDeltaInSeconds") int stateDeltaInSeconds,
            @JsonProperty("orderedStateVectors") String orderedStateVectors
    ) {
        satelliteInfo = new SatelliteInfo(norad, 'U', 0, 0, "A", 0);
        LocalDateTime startDateTime = LocalDateTime.of(
                LocalDate.of(1957, 12, 31).plusDays(startDay),
                LocalTime.ofSecondOfDay((long) startSecond));
        String[] states = orderedStateVectors.split("\n");
        List<SatelliteState> satelliteStates = new ArrayList<>();
        for (String state : states) {
            String[] coordinates = state.trim().split("\\s+");
            if (coordinates.length == 3) {
                try {
                    satelliteStates.add(new SatelliteState(startDateTime,
                            Double.parseDouble(coordinates[0]),
                            Double.parseDouble(coordinates[1]),
                            Double.parseDouble(coordinates[2])));
                } catch (Exception ex) {
                    log.error("Unexpected error during satellite state parse, line: " + state, ex);
                }
            }
            startDateTime = startDateTime.plusSeconds(stateDeltaInSeconds);
        }
        this.states = satelliteStates;

    }


    @Data
    @AllArgsConstructor
    public static class SatelliteState {
        private LocalDateTime dateTime;
        private double xCoord;
        private double yCoord;
        private double zCoord;
    }
}
