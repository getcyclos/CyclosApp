package ve.cyclos.fitness.util.io.general;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import ve.cyclos.fitness.data.Workout;
import ve.cyclos.fitness.data.WorkoutSample;

public interface IWorkoutExporter {
    void exportWorkout(Workout workout, List<WorkoutSample> samples, OutputStream outputStream) throws IOException;

    default void exportWorkout(Workout workout, List<WorkoutSample> samples, File file) throws IOException
    {
        exportWorkout(workout, samples, new FileOutputStream(file));
    }
}
