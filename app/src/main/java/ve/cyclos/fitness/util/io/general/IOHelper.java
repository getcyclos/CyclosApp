package ve.cyclos.fitness.util.io.general;

import ve.cyclos.fitness.util.io.GpxExporter;
import ve.cyclos.fitness.util.io.GpxImporter;

public final class IOHelper {
    public static final IWorkoutExporter GpxExporter = new GpxExporter();
    public static final IWorkoutImporter GpxImporter = new GpxImporter();
}
