package ve.cyclos.fitness.util.unit;

public class TimeFormatter {

    public static String formatDuration(long durationInS)
    {
        String formatted="";
        // get Hours (unlikely, but hey)
        if (durationInS / 3600.0 >= 1)
            return String.format(
                    "%d:%02d:%02d",
                    durationInS / 3600,
                    (durationInS % 3600) / 60,
                    durationInS % 60);

        return String.format(
                "%d:%02d",
                (durationInS % 3600) / 60,
                durationInS % 60);
    }
}
