package ve.cyclos.fitness.util.sections;

import java.util.ArrayList;
import java.util.List;

import ve.cyclos.fitness.util.unit.DistanceUnitUtils;
import ve.cyclos.fitness.util.unit.UnitUtils;

public class DistanceTranslator {
    private final DistanceUnitUtils distanceUnitUtils;
    private final SectionListModel listModel;

    public DistanceTranslator(DistanceUnitUtils distanceUnitUtils, SectionListModel listModel) {
        this.distanceUnitUtils = distanceUnitUtils;
        this.listModel = listModel;
    }

    public List<String> distanceUnits() {
        List<String> distUnits = new ArrayList<>();
        distUnits.add(distanceUnitUtils.getDistanceUnitSystem().getLongDistanceUnit());
        distUnits.add(distanceUnitUtils.getDistanceUnitSystem().getShortDistanceUnit());
        distUnits.add("#");
        return distUnits;
    }

    public List<String> timeUnits() {
        List<String> timeUnits = new ArrayList<>();
        timeUnits.add("h");
        timeUnits.add("min");
        timeUnits.add("s");
        timeUnits.add("#");
        return timeUnits;
    }

    public int getDefaultValueForTime(int unit) {
        switch (unit) {
            case 0: // h
                return 1;
            case 1: // min
                return 5;
            case 2: // s
                return 30;
            case 3: // #
            default:
                return 5;
        }
    }

    public int getDefaultValueForDistance(int unit) {
        switch (unit) {
            case 0: // km / miles / ...
                return 1;
            case 1: // m / yd / ...
                return 100;
            case 2: // #
            default:
                return 5;
        }
    }

    public double getNormalizedLength(SectionListModel.SectionCriterion criterion, int selectedUnit, double length) {
        switch (criterion) {
            case TIME: // for time like units, use as standard microseconds
                if (selectedUnit == 0) // h
                    length *= 3600000;
                else if (selectedUnit == 1) // min
                    length *= 60000;
                else if (selectedUnit == 2) // s
                    length *= 1000;
                else if (selectedUnit == 3) // #
                    length = listModel.getWorkout().duration / length;
                break;
            case DISTANCE: // For Distance (like) cases, transform to standard "meters"
            case ASCENT:
            case DESCENT:
                if (selectedUnit == 0) // km/miles/...
                    length = distanceUnitUtils.getDistanceUnitSystem().getMetersFromLongDistance(length);
                else if (selectedUnit == 1) // m/yd/...
                    length = distanceUnitUtils.getDistanceUnitSystem().getMetersFromShortDistance(length);
                else if (selectedUnit == 2) // #
                    if (criterion == SectionListModel.SectionCriterion.DISTANCE)
                        length = listModel.getWorkout().length / length; // Transform num sections to simple distance criterion
                    else if (criterion == SectionListModel.SectionCriterion.ASCENT)
                        length = listModel.getWorkout().ascent / length;
                    else if (criterion == SectionListModel.SectionCriterion.DESCENT)
                        length = listModel.getWorkout().descent / length;
                break;
        }
        return length;
    }

    public double getDisplayLength(SectionListModel.SectionCriterion criterion, int selectedUnit, double length) {
        switch (criterion) {
            case TIME: // for time like units, use as standard microseconds
                if (selectedUnit == 0) // h
                    length /= 3600000;
                else if (selectedUnit == 1) // min
                    length /= 60000;
                else if (selectedUnit == 2) // s
                    length /= 1000;
                else if (selectedUnit == 3) // #
                    length = listModel.getWorkout().duration / length;
                break;
            case DISTANCE: // For Distance (like) cases, transform to standard "meters"
            case ASCENT:
            case DESCENT:
                if (selectedUnit == 0) // km/miles/...
                    length = distanceUnitUtils.getDistanceUnitSystem().getDistanceFromKilometers(length / 1000);
                else if (selectedUnit == 1) // m/yd/...
                    length = distanceUnitUtils.getDistanceUnitSystem().getDistanceFromMeters(length);
                else if (selectedUnit == 2) // #
                    if (criterion == SectionListModel.SectionCriterion.DISTANCE)
                        length = listModel.getWorkout().length / length; // Transform num sections to simple distance criterion
                    else if (criterion == SectionListModel.SectionCriterion.ASCENT)
                        length = listModel.getWorkout().ascent / length;
                    else if (criterion == SectionListModel.SectionCriterion.DESCENT)
                        length = listModel.getWorkout().descent / length;
                break;
        }
        return UnitUtils.roundDouble(length, 2);
    }
}
