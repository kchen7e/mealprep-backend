package com.mealprep.MealPrep.entities.api.user;


import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
public class UserDTO {
    @Getter
    private String userName;



    public UserDTO(@NotNull String userName) {
        this.userName = userName.toLowerCase();
    }

}
