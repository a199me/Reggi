package com.gakki.reggie.DTO;

import com.gakki.reggie.entity.Setmeal;
import com.gakki.reggie.entity.SetmealDish;

import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
