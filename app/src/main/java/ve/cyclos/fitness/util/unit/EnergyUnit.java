package ve.cyclos.fitness.util.unit;

import androidx.annotation.StringRes;

public interface EnergyUnit {

    double getEnergy(double energyInKcal);

    String getInternationalShortName();

    @StringRes
    int getLongNameTitle();

}
