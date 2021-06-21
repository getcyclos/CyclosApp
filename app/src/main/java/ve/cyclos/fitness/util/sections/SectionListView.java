package ve.cyclos.fitness.util.sections;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ve.cyclos.fitness.Instance;
import ve.cyclos.fitness.R;
import ve.cyclos.fitness.util.unit.DistanceUnitUtils;

public class SectionListView extends LinearLayout implements SectionListContract.IListView {
    private LinearLayout listViews;
    private Spinner unitSpinner;
    private EditText lengthEdit;
    private Spinner typeSpinner;
    private TextView tableHeaderSelected;
    LinearLayout paceSpeedToggleLayout;

    private final List<SectionListContract.IListViewListener> listeners = new ArrayList<>();

    public SectionListView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        inflate(getContext(), R.layout.view_sections_list, this);
        listViews = findViewById(R.id.section_list);
        unitSpinner = findViewById(R.id.sectionLengthUnit);
        lengthEdit = findViewById(R.id.sectionLengthEdit);
        typeSpinner = findViewById(R.id.sectionTypeSpinner);
        tableHeaderSelected = findViewById(R.id.selectedCriterion);
        paceSpeedToggleLayout = findViewById(R.id.paceSpeedText);

        lengthEdit.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                lengthEdit.clearFocus();
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(lengthEdit.getWindowToken(), 0);
                return true;
            }
            return false;
        });

        lengthEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                for (SectionListContract.IListViewListener listener : listeners) {
                    double length = Double.parseDouble(lengthEdit.getText().toString());
                    listener.sectionLengthChanged(length);
                }
            }
        });

        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                for (SectionListContract.IListViewListener listener : listeners) {
                    listener.sectionUnitChanged(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SectionListModel.SectionCriterion criterion = SectionListModel.SectionCriterion.values()[i];
                for (SectionListContract.IListViewListener listener : listeners) {
                    listener.sectionCriterionChanged(criterion);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        paceSpeedToggleLayout.setOnClickListener(view -> {
            TextView viewSelected = findViewById(R.id.selectedPaceSpeed);
            TextView viewUnselected = findViewById(R.id.unselectedPaceSpeed);
            String formerSelected = viewSelected.getText().toString();
            viewSelected.setText(viewUnselected.getText());
            viewUnselected.setText(formerSelected);
            for (SectionListContract.IListViewListener listener : listeners) {
                listener.displayPaceToggled();
            }
        });
    }

    @Override
    public DistanceUnitUtils getDistanceUnitUtils() {
        return Instance.getInstance(getContext()).distanceUnitUtils;
    }

    @Override
    public List<String> getLocalizedTypeNames() {
        return SectionListModel.SectionCriterion.getStringRepresentations(this.getContext());
    }

    @Override
    public void displaySections(List<SectionListModel.Section> sections, boolean paceToggle, SectionListModel.SectionCriterion criterion) {
        SectionAdapter adapter = new SectionAdapter(sections, paceToggle, criterion, getContext());
        listViews.removeAllViews();
        for (int i = 0; i < adapter.getCount(); ++i) {
            View item = adapter.getView(i, null, listViews);
            listViews.addView(item);
        }
    }

    @Override
    public void setUnits(List<String> units) {
        unitSpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, units));
    }

    @Override
    public void setSectionTypes(List<String> types) {
        typeSpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, types));
    }

    @Override
    public void setSelectedUnit(int i) {
        unitSpinner.setSelection(i);
    }

    @Override
    public void setSectionLengthEditText(double length) {
        lengthEdit.setText(String.valueOf(length));
        for (SectionListContract.IListViewListener listener : listeners) {
            listener.sectionLengthChanged(length);
        }
    }

    @Override
    public void setSectionType(SectionListModel.SectionCriterion criterion) {
        if (typeSpinner.getCount() > criterion.getId())
            typeSpinner.setSelection(criterion.getId());
    }

    @Override
    public void setSelectedUnitColumnHeader(SectionListModel.SectionCriterion criterion) {
        String text = SectionListModel.SectionCriterion.getStringRepresentations(getContext()).get(criterion.getId());
        tableHeaderSelected.setText(text);
    }

    @Override
    public void subscribe(SectionListContract.IListViewListener listener) {
        listeners.add(listener);
    }
}
