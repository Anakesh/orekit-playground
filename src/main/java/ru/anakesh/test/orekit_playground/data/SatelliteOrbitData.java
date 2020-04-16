package ru.anakesh.test.orekit_playground.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
public class SatelliteOrbitData {
    private SatelliteInfo satelliteInfo;
    private Frame frame;
    private Set<SatelliteState> states;

    public SatelliteOrbitData(SatelliteInfo satelliteInfo, Set<SatelliteState> states) {
        this.satelliteInfo = satelliteInfo;
        this.states = states;
        this.frame = FramesFactory.getTEME();
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
