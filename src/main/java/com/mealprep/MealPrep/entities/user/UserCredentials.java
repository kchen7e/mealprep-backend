package com.mealprep.MealPrep.entities.user;

import jakarta.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
  @JoinColumn(name = "account", referencedColumnName = "username")
  private String userName;

  @Getter
  @Setter
  @Column(name = "password")
  private String password;

  @ElementCollection private Set<Token> tokens;

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
    this.tokens = new HashSet<>();
  }

  public void addToken(@NonNull Token token) {
    this.tokens.add(token);
  }

  public void invalidateToken(@NonNull Token token) {
    tokens.removeIf(target -> target.tokenEqual(token));
  }

  public void invalidateToken(@NonNull String token) {
    tokens.removeIf(target -> target.tokenEqual(token));
  }

  public boolean validateToken(@NonNull Token token) {
    return tokens.contains(token);
  }

  public boolean validateToken(@NonNull String token) {
    return tokens.stream().anyMatch(existingToken -> existingToken.tokenEqual(token));
  }
}
