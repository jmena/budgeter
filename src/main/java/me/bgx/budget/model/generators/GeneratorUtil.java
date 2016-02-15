package me.bgx.budget.model.generators;

import me.bgx.budget.model.generators.capitalinterestgen.CapitalInterestGenerator;
import me.bgx.budget.model.generators.capitalinterestgen.DetailedCapitalInterestGenerator;
import me.bgx.budget.model.generators.capitalinterestgen.SingleCapitalInterestGenerator;

class GeneratorUtil {
    public static CapitalInterestGenerator newCapitalInterestGenerator(boolean isDetailed) {
        if (isDetailed) {
            return new DetailedCapitalInterestGenerator();
        } else {
            return new SingleCapitalInterestGenerator();
        }
    }
}
