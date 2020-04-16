package ru.anakesh.test.orekit_playground;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.PVCoordinates;
import ru.anakesh.test.orekit_playground.data.SatelliteInfo;
import ru.anakesh.test.orekit_playground.data.SatelliteOrbitData;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SatelliteServiceTest {
    private final SatelliteService satelliteService = new SatelliteService();

    @BeforeAll
    static void initializeOrekit() {
        OrekitUtils.initialize();
    }

    @Test
    void tleBackAndForthConversionTest() {
        TLE tle = new TLE(
                "1 25544U 98067A   20101.12277921 -.00013072  00000-0 -23215-3 0  9996",
                "2 25544  51.6452 325.2970 0003764  98.5553  39.4188 15.48670570221469");
        convertSatelliteStateSetToTle(tle);
        tle = new TLE(
                "1 25019U 97065A   20106.43432361 -.00000072  00000-0  00000-0 0  9999",
                "2 25019  10.1495  36.4931 0007539 323.1063 248.6002  1.00269607 10359");
        convertSatelliteStateSetToTle(tle);
        tle = new TLE(
                "1 44226U 19026B   20106.93963795  .00001457  00000-0  70164-4 0  9995",
                "2 44226  40.0217   2.9870 0012926 297.6765  62.2723 15.22064404 52818");
        convertSatelliteStateSetToTle(tle);
        tle = new TLE(
                "1 22049U 92044A   20079.12041623 -.00001625  00000-0  00000+0 0  9997",
                "2 22049  27.3426  49.9721 5668028 165.4055 357.5067  0.19209054 11371");
        convertSatelliteStateSetToTle(tle);
        tle = new TLE(
                "1 38995U 12063A   20107.10636899  .00000137  00000-0  10000-3 0  9993",
                "2 38995  65.3553 234.6477 6491097 264.0356 168.1309  2.00605307 54367");
        convertSatelliteStateSetToTle(tle);
    }

    private void convertSatelliteStateSetToTle(@NotNull TLE tle) {
        SatelliteOrbitData satelliteOrbitData = generateSatelliteOrbitData(tle, 1440, 10);
        TLE convertedTle = satelliteService.convertSatelliteStateSetToTle(satelliteOrbitData);
        assertEquals(tle.getBStar(), convertedTle.getBStar(), 1e-4d, "BStar drag coefficient is not the same");
        assertEquals(tle.getI(), convertedTle.getI(), 1e-11d, "Inclination is not the same");
        assertEquals(tle.getRaan(), convertedTle.getRaan(), 1e-11d, "RAAN (Right Ascension of the Ascending Node) is not the same");
        assertEquals(tle.getE(), convertedTle.getE(), 1e-7d, "Eccentricity is not the same");
        assertEquals(tle.getPerigeeArgument(), convertedTle.getPerigeeArgument(), 1e-11d, "Argument of Perigee is not the same");
        assertEquals(tle.getMeanAnomaly(), convertedTle.getMeanAnomaly(), 1e-11d, "Mean Anomaly is not the same");
        assertEquals(tle.getMeanMotion(), convertedTle.getMeanMotion(), 1e-13d, "Mean Motion is not the same");
    }

    @NotNull
    private SatelliteOrbitData generateSatelliteOrbitData(@NotNull TLE sourceTle, int numberOfStates, int timeStepInSeconds) {
        TLEPropagator tlePropagator = TLEPropagator.selectExtrapolator(sourceTle);
        Set<SatelliteOrbitData.SatelliteState> states = new HashSet<>();
        AbsoluteDate date = sourceTle.getDate();
        for (int i = 0; i < numberOfStates; i++) {
            PVCoordinates pvCoordinates = tlePropagator.getPVCoordinates(date);
            Vector3D position = pvCoordinates.getPosition();
            LocalDateTime dateTime = LocalDateTime.ofInstant(date.toDate(TimeScalesFactory.getUTC()).toInstant(), ZoneOffset.UTC);
            states.add(new SatelliteOrbitData.SatelliteState(dateTime, position.getX(), position.getY(), position.getZ()));
            date = date.shiftedBy(timeStepInSeconds);
        }
        SatelliteInfo satelliteInfo = new SatelliteInfo(sourceTle.getSatelliteNumber(), sourceTle.getClassification(), sourceTle.getLaunchYear(),
                sourceTle.getLaunchNumber(), sourceTle.getLaunchPiece(), sourceTle.getElementNumber());
        return new SatelliteOrbitData(satelliteInfo, states);
    }


}