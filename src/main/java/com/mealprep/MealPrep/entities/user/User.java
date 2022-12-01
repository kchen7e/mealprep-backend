package com.mealprep.MealPrep.entities.user;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "account")
public class User {

    @Id
    @Column(name = "user_name")
    @JsonProperty("userName")
    private String userName;

    @Column(name = "email")
    @JsonProperty("email")
    private String email;


    @Setter
    @Column(name = "first_name")
    @JsonProperty("firstName")
    private String firstName;

    @Setter
    @Column(name = "last_name")
    @JsonProperty("lastName")
    private String lastName;


    @Setter
    @Column(name = "country")
    @JsonProperty("country")
    private String country;


    public User(String userName) {
        this.userName = userName.toLowerCase();
    }

    public void setUserName(String userName) {
        if (StringUtils.isNotBlank(userName)) {
        this.userName = userName.toLowerCase();
        }
    }

    public void setEmail(String email) {
        if (StringUtils.isNotBlank(email)) {
        this.email = email.toLowerCase();
        }
    }

}
