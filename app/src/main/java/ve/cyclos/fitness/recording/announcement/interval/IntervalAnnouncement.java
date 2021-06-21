package ve.cyclos.fitness.recording.announcement.interval;

import ve.cyclos.fitness.data.Interval;
import ve.cyclos.fitness.recording.WorkoutRecorder;
import ve.cyclos.fitness.recording.announcement.Announcement;

public class IntervalAnnouncement implements Announcement {

    private final Interval interval;

    public IntervalAnnouncement(Interval interval) {
        this.interval = interval;
    }

    @Override
    public boolean isAnnouncementEnabled() {
        return true;
    }

    @Override
    public String getSpokenText(WorkoutRecorder recorder) {
        return interval.name;
    }

}
