package com.mealprep.MealPrep.entities.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Embeddable
@Getter
@NoArgsConstructor
public class Token {

  private String token;

  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_at", updatable = false)
  private Date createdAt;

  public Token(String token) {
    this.token = token;
  }

  @Override
  public int hashCode() {
    return token.hashCode();
  }

  // if all fields equal
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Token) {
      return (token.equals(((Token) obj).getToken()));
    } else {
      return super.equals(obj);
    }
  }

  public boolean tokenEqual(Token token) {
    return this.token.equals(token.getToken());
  }

  public boolean tokenEqual(String tokenStr) {
    return token.equals(tokenStr);
  }
}
