package com.mealprep.MealPrep.measures;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.util.NoSuchElementException;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class Unit {
    public enum Type {
        MG, G, KG, ML, L, GAL, C, PT, QT, TSP, TBSP
    }
    private Float measure;
    private Type type;

    public Unit() {
        measure = (float) 0;
        type = Type.KG;
    }

    public Unit(String type, String measure) throws
            NoSuchElementException, NumberFormatException {
        if (type != null) {
        this.type = Type.valueOf(type);
        } else {
            this.type = Type.G;
        }
        this.measure = Float.valueOf(Objects.requireNonNullElse(measure, "0"));
    }

    public Unit(String type, Float measure) throws NoSuchElementException,
            NumberFormatException {
        if (type != null) {
            this.type = Type.valueOf(type);
        } else {
            this.type = Type.G;
        }
        this.measure = measure;
    }
}