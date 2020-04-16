package ru.anakesh.test.orekit_playground;

import org.jetbrains.annotations.NotNull;
import org.orekit.data.ClasspathCrawler;
import org.orekit.data.DataContext;
import org.orekit.data.DataProvidersManager;
import org.orekit.estimation.iod.IodGibbs;
import org.orekit.frames.Frame;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngle;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.propagation.conversion.AbstractPropagatorBuilder;
import org.orekit.propagation.conversion.FiniteDifferencePropagatorConverter;
import org.orekit.propagation.conversion.TLEPropagatorBuilder;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.AbsolutePVCoordinates;
import org.orekit.utils.Constants;
import ru.anakesh.test.orekit_playground.data.SatelliteInfo;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class OrekitUtils {
    private static boolean initialized = false;

    public synchronized static void initialize() {
        if (!initialized) {
            DataProvidersManager manager = DataContext.getDefault().getDataProvidersManager();
            manager.addProvider(new ClasspathCrawler(OrekitUtils.class.getClassLoader(), "orekit/orekit-data-master.zip"));
            initialized = true;
        }
    }

    @NotNull
    public AbsoluteDate convertLocalDateTimeToAbsoluteDate(@NotNull LocalDateTime localDateTime) {
        return new AbsoluteDate(Date.from(localDateTime.toInstant(ZoneOffset.UTC)), TimeScalesFactory.getUTC());
    }

    @NotNull
    public TLE constructTleFromSpaceCraftStates(@NotNull Frame frame, @NotNull SatelliteInfo satelliteInfo, @NotNull List<SpacecraftState> spacecraftStates) {
        if (spacecraftStates.size() < 3)
            throw new IllegalArgumentException("Can't construct TLE from less than 3 spacecraft states");
        IodGibbs iodGibbs = new IodGibbs(Constants.WGS84_EARTH_MU);
        List<SpacecraftState> statesInSelectedFrame = new ArrayList<>();
        spacecraftStates.stream().sorted(Comparator.comparing(SpacecraftState::getDate)).forEach(state -> {
            if (state.getFrame().equals(frame)) {
                statesInSelectedFrame.add(state);
            } else {
                statesInSelectedFrame.add(new SpacecraftState(new AbsolutePVCoordinates(frame, state.getPVCoordinates(frame))));
            }
        });
        SpacecraftState firstState = statesInSelectedFrame.get(0);
        SpacecraftState secondState = statesInSelectedFrame.get(1);
        SpacecraftState thirdState = statesInSelectedFrame.get(2);
        KeplerianOrbit orbit = iodGibbs.estimate(frame, firstState.getPVCoordinates().getPosition(), firstState.getDate(),
                secondState.getPVCoordinates().getPosition(), secondState.getDate(),
                thirdState.getPVCoordinates().getPosition(), thirdState.getDate());
        TLE ssTle = new TLE(satelliteInfo.getNorad(), satelliteInfo.getClassification(), satelliteInfo.getLaunchYear(), satelliteInfo.getLaunchNumber(), satelliteInfo.getLaunchPiece(), 0,
                satelliteInfo.getElementNumber(), firstState.getDate(), orbit.getKeplerianMeanMotion(), 0d, 0d,
                orbit.getE(), orbit.getI(), orbit.getPerigeeArgument(), orbit.getRightAscensionOfAscendingNode(), orbit.getMeanAnomaly(),
                0, 0d);
        AbstractPropagatorBuilder builder =
                new TLEPropagatorBuilder(ssTle, PositionAngle.TRUE, 1.0);
        double threshold = 1.0e-3;
        FiniteDifferencePropagatorConverter fitter = new FiniteDifferencePropagatorConverter(builder, threshold, 1000);
        fitter.convert(spacecraftStates, true, TLEPropagatorBuilder.B_STAR);
        TLEPropagator prop = (TLEPropagator) fitter.getAdaptedPropagator();
        return prop.getTLE();
    }
}
