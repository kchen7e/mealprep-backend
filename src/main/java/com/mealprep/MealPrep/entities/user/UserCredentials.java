package com.mealprep.MealPrep.entities.user;


import com.mealprep.MealPrep.entities.user.Token;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Columns;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "user_credentials")
@NoArgsConstructor
public class UserCredentials {

    @Id
    @Getter
    @JoinColumn(name = "account", referencedColumnName = "user_name")
    private String userName;

    @Getter
    @Column(name = "password")
    private String password;

    @ElementCollection
    private Set<Token> tokens;

    @CreationTimestamp
    @Getter
    @Column(name = "created_at", updatable = false)//hibernate BUG, column is
    // blank if "updatable set to false" is missing
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @UpdateTimestamp
    @Getter
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public UserCredentials(@NotNull String userName, @NotNull String password) {
        this.userName = userName.toLowerCase();
        this.password = password;
        this.tokens = new HashSet<>();
    }

    public void addToken(@NotNull Token token) {
        this.tokens.add(token);
    }

    public void invalidateToken(@NotNull Token token) {
        tokens.removeIf(target -> target.tokenEqual(token));
    }

    public boolean validateToken(@NotNull Token token) {
        return tokens.contains(token);
    }

    public boolean validateToken(@NotNull String token) {
        return tokens.stream().anyMatch(existingToken ->
            existingToken.tokenEqual(token)
        );
    }
}
