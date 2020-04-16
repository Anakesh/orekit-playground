package ru.anakesh.test.orekit_playground.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
public class SatelliteInfo {
    private int norad;
    private char classification;
    private int launchYear;
    private int launchNumber;
    @NotNull
    private String launchPiece;
    private int elementNumber;
}
