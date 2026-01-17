package com.mealprep.MealPrep.entities.market;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Embeddable
@RequiredArgsConstructor
public class CommercialForm {

  public enum SaleUnitType {
    BAG,
    CAN,
    BOX,
    N_PACK,
    PIECE,
    LOAF,
    KG,
    G
  }

  //    @JoinColumn(name = "food", referencedColumnName = "food_name")
  //    @Getter
  //    private String foodName;

  // on shelf as a bag, can etc.
  @Getter
  @Setter
  @Column(name = "purchase_unit")
  SaleUnitType purchaseUnit;

  @Getter
  @Setter
  @Column(name = "available_locations")
  String foundAt;

  // e.g. 250ml per can
  @Getter
  @Setter
  @Column(name = "average_measure_per_purchase")
  int averageMeasurePerPurchase;

  public CommercialForm(SaleUnitType purchaseUnit, int averageMeasurePerPurchase) {
    this.purchaseUnit = purchaseUnit;
    this.averageMeasurePerPurchase = averageMeasurePerPurchase;
  }

  public CommercialForm(SaleUnitType purchaseUnit, int averageMeasurePerPurchase, String foundAt) {
    this.purchaseUnit = purchaseUnit;
    this.averageMeasurePerPurchase = averageMeasurePerPurchase;
    this.foundAt = foundAt;
  }

  @Override
  public int hashCode() {
    return purchaseUnit.hashCode()
        + ((String.valueOf(averageMeasurePerPurchase)).concat(foundAt)).hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof CommercialForm) {
      return purchaseUnit.equals(((CommercialForm) obj).getPurchaseUnit())
          && averageMeasurePerPurchase == ((CommercialForm) obj).getAverageMeasurePerPurchase()
          && foundAt.equals(((CommercialForm) obj).foundAt);
    } else {
      return super.equals(obj);
    }
  }
}
