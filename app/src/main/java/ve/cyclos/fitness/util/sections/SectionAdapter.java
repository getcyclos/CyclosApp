package ve.cyclos.fitness.util.sections;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import ve.cyclos.fitness.Instance;
import ve.cyclos.fitness.R;
import ve.cyclos.fitness.util.unit.DistanceUnitUtils;
import ve.cyclos.fitness.util.unit.TimeFormatter;

public class SectionAdapter extends ArrayAdapter<SectionListModel.Section> {
    private final boolean paceToggle;
    private final DistanceUnitUtils distanceUnitUtils;
    private final SectionListModel.SectionCriterion criterion;
    private final Context mContext;
    private final List<SectionListModel.Section> sections;

    private boolean switchRows = true;
    private double accumulatedCriterionValue = 0;
    private double worstPace = 0;
    private double bestPace = 0;

    // View lookup cache
    private static class ViewHolder {
        TextView dist;
        View distLayout;
        TextView time;
        View timeLayout;
        TextView mDown;
        View downLayout;
        TextView mUp;
        View upLayout;
        TextView pace;
        TextView criterionText;
        View progressBg;
    }

    public SectionAdapter(List<SectionListModel.Section> sections, boolean paceToggle, SectionListModel.SectionCriterion criterion, Context context) {
        super(context, R.layout.section_entry, sections);
        this.sections = sections;
        this.paceToggle = paceToggle;
        this.criterion = criterion;
        this.mContext = context;
        distanceUnitUtils = Instance.getInstance(context).distanceUnitUtils;

        for (SectionListModel.Section section : sections) {
            if (section.worst)
                worstPace = section.getPace();
            if (section.best)
                bestPace = section.getPace();
        }
    }

    private final int lastPosition = -1;

    @Override
    public View getView(int position, View sectionView, ViewGroup parent) {
        // Get the data item for this position
        SectionListModel.Section section = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (sectionView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            sectionView = inflater.inflate(R.layout.section_entry, parent, false);
            viewHolder.dist = sectionView.findViewById(R.id.sectionDist);
            viewHolder.distLayout = sectionView.findViewById(R.id.distLayout);
            viewHolder.time = sectionView.findViewById(R.id.sectionTime);
            viewHolder.timeLayout = sectionView.findViewById(R.id.timeLayout);
            viewHolder.mDown = sectionView.findViewById(R.id.sectionDescent);
            viewHolder.downLayout = sectionView.findViewById(R.id.descentLayout);
            viewHolder.mUp = sectionView.findViewById(R.id.sectionAscent);
            viewHolder.upLayout = sectionView.findViewById(R.id.ascentLayout);
            viewHolder.pace = sectionView.findViewById(R.id.sectionPace);
            viewHolder.criterionText = sectionView.findViewById(R.id.sectionCrit);
            viewHolder.progressBg = sectionView.findViewById(R.id.progress1);

            if (switchRows) {
                sectionView.setBackgroundColor(getContext().getResources().getColor(R.color.lineHighlight));
                sectionView.getBackground().setAlpha(127);
            }
            switchRows = !switchRows;

            sectionView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) sectionView.getTag();
        }

        assert section != null;
        float progress = (float) (section.getPace() / worstPace);
        if (!paceToggle)
            progress = (float) (bestPace / section.getPace());
        setProgress(sectionView, progress);

        if (section.worst) {
            viewHolder.progressBg.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
            viewHolder.progressBg.getBackground().setAlpha(63);
        }
        if (section.best) {
            viewHolder.progressBg.setBackgroundColor(getContext().getResources().getColor(R.color.colorAccent));
            viewHolder.progressBg.getBackground().setAlpha(63);
        }

        viewHolder.dist.setText(roundMeterToKilometer(section.dist) + distanceUnitUtils.getDistanceUnitSystem().getLongDistanceUnit());
        viewHolder.time.setText(TimeFormatter.formatDuration((long) (section.time / 1000)));
        viewHolder.mDown.setText(Math.round(section.descent) + distanceUnitUtils.getDistanceUnitSystem().getShortDistanceUnit());
        viewHolder.mUp.setText(Math.round(section.ascent) + distanceUnitUtils.getDistanceUnitSystem().getShortDistanceUnit());
        if (paceToggle)
            viewHolder.pace.setText(TimeFormatter.formatDuration((long) (section.getPace())));
        else
            viewHolder.pace.setText(distanceUnitUtils.getSpeed(1000.0 / section.getPace())); // must be in m/s


        switch (criterion) {
            case ASCENT:
                accumulatedCriterionValue += section.ascent;
                viewHolder.criterionText.setText(String.valueOf(Math.round(accumulatedCriterionValue)));
                viewHolder.upLayout.setVisibility(View.GONE);
                break;
            case DESCENT:
                accumulatedCriterionValue += section.descent;
                viewHolder.criterionText.setText(String.valueOf(Math.round(accumulatedCriterionValue)));
                viewHolder.downLayout.setVisibility(View.GONE);
                break;
            case TIME:
                accumulatedCriterionValue += section.time;
                viewHolder.criterionText.setText(TimeFormatter.formatDuration((long) (accumulatedCriterionValue / 1000)));
                viewHolder.timeLayout.setVisibility(View.GONE);
                break;
            case DISTANCE:
            default:
                accumulatedCriterionValue += section.dist;
                viewHolder.criterionText.setText(String.valueOf(roundMeterToKilometer(accumulatedCriterionValue)));
                viewHolder.distLayout.setVisibility(View.GONE);
                break;
        }
        // Return the completed view to render on screen
        return sectionView;
    }

    private void setProgress(View sectionEntry, float percentage) {
        View v1 = sectionEntry.findViewById(R.id.progress1);
        v1.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                percentage * 8));
        View v2 = sectionEntry.findViewById(R.id.progress2);
        v2.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                (1 - percentage) * 8 + 6));
    }

    private double roundMeterToKilometer(double distance) {
        double distInKm = distance / (double) 1000;
        return Math.round(distanceUnitUtils.getDistanceUnitSystem().getDistanceFromKilometers(distInKm) * 100) / 100.0;
    }
}
