package com.mealprep.MealPrep.entities.user;

import jakarta.persistence.*;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "user_credentials")
@NoArgsConstructor
public class UserCredentials {

  @Id
  @Getter
  @Column(name = "username")
  private String userName;

  // Proper relationship to User (account table)
  @OneToOne
  @JoinColumn(
      name = "username",
      referencedColumnName = "username",
      insertable = false,
      updatable = false)
  @Getter
  private User user;

  @Getter
  @Setter
  @Column(name = "password")
  private String password;

  @CreationTimestamp
  @Getter
  @Column(name = "created_at", updatable = false) // hibernate BUG, column is
  // blank if "updatable set to false" is missing
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdAt;

  @UpdateTimestamp
  @Getter
  @Column(name = "updated_at")
  @Temporal(TemporalType.TIMESTAMP)
  private Date updatedAt;

  public UserCredentials(@NonNull String userName, @NonNull String password) {
    this.userName = userName.toLowerCase();
    this.password = password;
  }
}
