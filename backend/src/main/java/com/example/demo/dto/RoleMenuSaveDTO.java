package com.example.demo.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class RoleMenuSaveDTO {
    @NotEmpty
    private List<Long> menuIds;

    public List<Long> getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(List<Long> menuIds) {
        this.menuIds = menuIds;
    }
}
