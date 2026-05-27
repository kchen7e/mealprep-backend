package com.mealprep.MealPrep.measures;

import jakarta.persistence.Embeddable;
import java.util.EnumSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class Unit {
  public enum Type {
    MG,
    G,
    KG,
    ML,
    L,
    GAL,
    C,
    PT,
    QT,
    TSP,
    TBSP
  }

  private static final Set<Type> MASS_UNITS = EnumSet.of(Type.MG, Type.G, Type.KG);
  private static final Set<Type> VOLUME_UNITS =
      EnumSet.of(Type.ML, Type.L, Type.GAL, Type.C, Type.PT, Type.QT, Type.TSP, Type.TBSP);

  private Float measure;
  private Type type;

  public Unit() {
    measure = 0f;
    type = Type.G;
  }

  public Unit(Type type, Float measure) {
    this.type = type != null ? type : Type.G;
    this.measure = measure != null ? measure : 0f;
  }

  public Unit(String type, String measure) throws NoSuchElementException, NumberFormatException {
    if (type != null) {
      this.type = Type.valueOf(type);
    } else {
      this.type = Type.G;
    }
    this.measure = Float.valueOf(Objects.requireNonNullElse(measure, "0"));
  }

  public Unit(String type, Float measure) throws NoSuchElementException, NumberFormatException {
    if (type != null) {
      this.type = Type.valueOf(type);
    } else {
      this.type = Type.G;
    }
    this.measure = measure;
  }

  /** Checks if this unit can be combined with another unit (same family: mass or volume). */
  public boolean isCompatibleWith(Unit other) {
    if (other == null) return false;
    return (MASS_UNITS.contains(this.type) && MASS_UNITS.contains(other.type))
        || (VOLUME_UNITS.contains(this.type) && VOLUME_UNITS.contains(other.type));
  }

  /**
   * Combines this unit with another, returning a new Unit with the summed value. Both units must be
   * compatible (same family). Result uses the larger unit type. Returns a copy of this unit if
   * other is null or incompatible.
   */
  public Unit combine(Unit other) {
    if (other == null || !isCompatibleWith(other)) {
      return new Unit(this.type, this.measure);
    }

    float thisBase = toBaseUnit();
    float otherBase = other.toBaseUnit();
    float totalBase = thisBase + otherBase;

    Type resultType = selectResultType(totalBase);
    float resultMeasure = fromBaseUnit(totalBase, resultType);

    return new Unit(resultType, resultMeasure);
  }

  /** Converts this unit's measure to base unit (grams for mass, ml for volume). */
  private float toBaseUnit() {
    return switch (type) {
      case MG -> measure / 1000f;
      case G -> measure;
      case KG -> measure * 1000f;
      case ML -> measure;
      case L -> measure * 1000f;
      case GAL -> measure * 3785.41f;
      case C -> measure * 236.588f;
      case PT -> measure * 473.176f;
      case QT -> measure * 946.353f;
      case TSP -> measure * 4.929f;
      case TBSP -> measure * 14.787f;
    };
  }

  /** Converts from base unit to the specified type. */
  private static float fromBaseUnit(float baseValue, Type targetType) {
    return switch (targetType) {
      case MG -> baseValue * 1000f;
      case G -> baseValue;
      case KG -> baseValue / 1000f;
      case ML -> baseValue;
      case L -> baseValue / 1000f;
      case GAL -> baseValue / 3785.41f;
      case C -> baseValue / 236.588f;
      case PT -> baseValue / 473.176f;
      case QT -> baseValue / 946.353f;
      case TSP -> baseValue / 4.929f;
      case TBSP -> baseValue / 14.787f;
    };
  }

  /** Selects an appropriate result unit type based on the base value magnitude. */
  private Type selectResultType(float baseValue) {
    if (MASS_UNITS.contains(this.type)) {
      if (baseValue >= 1000f) return Type.KG;
      if (baseValue < 1f) return Type.MG;
      return Type.G;
    } else {
      if (baseValue >= 1000f) return Type.L;
      return Type.ML;
    }
  }
}
