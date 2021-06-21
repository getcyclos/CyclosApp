package ve.cyclos.fitness.util.sections;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ve.cyclos.fitness.R;
import ve.cyclos.fitness.data.Workout;
import ve.cyclos.fitness.data.WorkoutSample;

public class SectionListModel {
    private final Workout workout;
    private final List<WorkoutSample> samples;
    private List<String> units = new ArrayList<>();
    private SectionCriterion criterion = SectionCriterion.DISTANCE;
    private double sectionLength = 1;
    private int selectedUnitID = 1;
    private boolean paceToggle = true;

    public SectionListModel(Workout workout, List<WorkoutSample> samples) {
        this.samples = samples;
        this.workout = workout;
    }

    public void setCriterion(SectionCriterion criterion) {
        this.criterion = criterion;
        switch (criterion) {
            case TIME:
                setSelectedUnitID(1); // default: min
                break;
            case DISTANCE:
                setSelectedUnitID(0); // default: km / miles
                break;
            case ASCENT:
            case DESCENT:
                setSelectedUnitID(1); // default : m
                break;
        }
    }

    public SectionCriterion getCriterion() {
        return criterion;
    }

    public double getSectionLength() {
        return sectionLength;
    }

    public void setSectionLength(double length) {
        this.sectionLength = length;
    }

    public int getSelectedUnitID() {
        return selectedUnitID;
    }

    public void setSelectedUnitID(int unitID) {
        selectedUnitID = unitID;
    }

    public boolean getPaceToggle() {
        return paceToggle;
    }

    public void setPaceToggle(boolean toggle) {
        paceToggle = toggle;
    }

    public Workout getWorkout() {
        return workout;
    }

    public void setUnits(List<String> units) {
        this.units = units;
    }

    public List<String> getUnits() {
        return units;
    }

    public ArrayList<Section> getSectionList() {
        ArrayList<Section> sections = new ArrayList<>();

        Section currentSection = new Section();

        int best = 0;
        int worst = 0;

        for (int i = 1; i < samples.size(); ++i) {
            WorkoutSample sample = samples.get(i);
            WorkoutSample previous = samples.get(i - 1);

            currentSection.dist += sample.toLatLong().sphericalDistance(previous.toLatLong());
            currentSection.time += sample.relativeTime - previous.relativeTime;
            if (sample.elevation < previous.elevation)
                currentSection.descent += previous.elevation - sample.elevation;
            if (sample.elevation > previous.elevation)
                currentSection.ascent += sample.elevation - previous.elevation;

            double currentCriteriaLength;
            switch (criterion) {
                case ASCENT:
                    currentCriteriaLength = currentSection.ascent;
                    break;
                case DESCENT:
                    currentCriteriaLength = currentSection.descent;
                    break;
                case TIME:
                    currentCriteriaLength = currentSection.time;
                    break;
                case DISTANCE:
                default:
                    currentCriteriaLength = currentSection.dist;
                    break;
            }

            if (currentCriteriaLength >= sectionLength) {
                // interpolate to find actual values
                double interpolate = sectionLength/currentCriteriaLength;
                Section saveSection = currentSection.copy();
                saveSection.dist *= interpolate;
                saveSection.time *= interpolate;
                saveSection.descent *= interpolate;
                saveSection.ascent *= interpolate;

                sections.add(saveSection);
                // check for best / worst
                if (isSectionBetter(sections.get(worst), saveSection)) {
                    worst = sections.size() - 1;
                } else if (isSectionBetter(saveSection, sections.get(best))) {
                    best = sections.size() - 1;
                }


                // Set start values for new section considering inertpolated values
                Section cSection = new Section();
                cSection.dist = currentSection.dist - saveSection.dist;
                cSection.time =  currentSection.time - saveSection.time;
                cSection.ascent =  currentSection.ascent - saveSection.ascent;
                cSection.descent =  currentSection.descent - saveSection.descent;
                currentSection = cSection;
            }
        }


        // Last Section should have a length of at least 1m and a time of at least 1 sec
        if (currentSection.dist > 1 && currentSection.time > 1000) {
            sections.add(currentSection);
            if (isSectionBetter(sections.get(worst), currentSection)) {
                worst = sections.size() - 1;
            } else if (isSectionBetter(currentSection, sections.get(best))) {
                best = sections.size() - 1;
            }
        }

        sections.get(worst).worst = true;
        sections.get(best).best = true;

        return sections;
    }

    private static boolean isSectionBetter(Section section, Section compareTo) {
        return section.getPace() <= compareTo.getPace();
    }

    public enum SectionCriterion {
        DISTANCE(0),
        TIME(1),
        ASCENT(2),
        DESCENT(3);

        private final int id;

        SectionCriterion(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public static List<String> getStringRepresentations(Context context) {
            List<String> criteria = new ArrayList<>();
            criteria.add(context.getString(R.string.workoutDistance));
            criteria.add(context.getString(R.string.workoutTime));
            criteria.add(context.getString(R.string.workoutAscent));
            criteria.add(context.getString(R.string.workoutDescent));
            return criteria;
        }
    }

    public static class Section {
        double dist = 0, ascent = 0, descent = 0;
        double time = 0;
        boolean best = false;
        boolean worst = false;

        public double getPace() {
            return time / dist;
        }

        public Section copy() {
            Section section = new Section();
            section.dist = dist;
            section.ascent = ascent;
            section.descent = descent;
            section.time = time;
            section.best = best;
            section.worst = worst;
            return section;
        }
    }
}
