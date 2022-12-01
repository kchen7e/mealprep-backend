package com.mealprep.MealPrep.entities.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

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

    //if all fields equal
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
