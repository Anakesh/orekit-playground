package ru.anakesh.test.orekit_playground;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.jetbrains.annotations.NotNull;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.utils.AbsolutePVCoordinates;
import ru.anakesh.test.orekit_playground.data.SatelliteOrbitData;

import java.util.List;
import java.util.stream.Collectors;

public class SatelliteService {
    private OrekitUtils orekitUtils = new OrekitUtils();

    @NotNull
    public TLE convertSatelliteStateSetToTle(@NotNull SatelliteOrbitData satelliteOrbitData) {
        OrekitUtils.initialize();
        List<SpacecraftState> spacecraftStates = covertSatelliteOrbitDataToSpaceCraftStates(satelliteOrbitData);
        return orekitUtils.constructTleFromSpaceCraftStates(satelliteOrbitData.getFrame(), satelliteOrbitData.getSatelliteInfo(), spacecraftStates);
    }

    @NotNull
    public List<SpacecraftState> covertSatelliteOrbitDataToSpaceCraftStates(@NotNull SatelliteOrbitData satelliteOrbitData) {
        return satelliteOrbitData.getStates().stream().map(s -> new SpacecraftState(
                new AbsolutePVCoordinates(
                        satelliteOrbitData.getFrame(),
                        orekitUtils.convertLocalDateTimeToAbsoluteDate(s.getDateTime()),
                        new Vector3D(s.getXCoord(), s.getYCoord(), s.getZCoord()),
                        Vector3D.NaN))
        ).collect(Collectors.toList());
    }

}
