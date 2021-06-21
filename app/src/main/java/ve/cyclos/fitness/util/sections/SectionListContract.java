package ve.cyclos.fitness.util.sections;

import java.util.List;

import ve.cyclos.fitness.util.unit.DistanceUnitUtils;

public interface SectionListContract {

    interface IListViewListener {
        void displayPaceToggled();

        void sectionLengthChanged(double length);

        void sectionUnitChanged(int selectedUnitID);

        void sectionCriterionChanged(SectionListModel.SectionCriterion criterion);
    }

    interface IListView {
        DistanceUnitUtils getDistanceUnitUtils(); // TODO: Make DistanceUnitContext independent?!

        List<String> getLocalizedTypeNames();

        // View interactions
        void displaySections(List<SectionListModel.Section> list, boolean paceToggle, SectionListModel.SectionCriterion criterion);

        void setUnits(List<String> units);

        void setSectionTypes(List<String> types);

        void setSelectedUnit(int i);

        void setSectionLengthEditText(double length);

        void setSectionType(SectionListModel.SectionCriterion criterion);

        void setSelectedUnitColumnHeader(SectionListModel.SectionCriterion criterion);

        void subscribe(IListViewListener listener);
    }
}
