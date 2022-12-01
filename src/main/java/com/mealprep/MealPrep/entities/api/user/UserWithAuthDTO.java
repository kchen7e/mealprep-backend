package com.mealprep.MealPrep.entities.api.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mealprep.MealPrep.entities.user.User;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Base64;


@Getter
public class UserWithAuthDTO extends User {
    @Setter
    @JsonProperty("email")
    private String email;

    @Setter
    @JsonProperty("firstName")
    private String firstName;

    @Setter
    @JsonProperty("lastName")
    private String lastName;


    @Setter
    @JsonProperty("country")
    private String country;


    @JsonProperty("password")
    private transient String password;

    @Setter
    @JsonProperty("token")
    private String token;



//    public NewUserDTO(String userName) {
//        super(userName);
//    }

//    public NewUserDTO(String userName, String password) {
//        super(userName);
//        this.password = password;
//    }


//    public String getPassword() {
//        return Base64.getEncoder().encodeToString(password.getBytes());
//    }

    public void setPassword(String passwordBase64) {
        if (StringUtils.isNotBlank(passwordBase64)) {
        this.password = Arrays.toString(Base64.getDecoder().decode(
                passwordBase64));
        } else {
            password = "";
        }
    }
}
