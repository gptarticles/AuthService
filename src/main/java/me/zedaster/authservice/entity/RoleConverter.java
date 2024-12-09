package me.zedaster.authservice.entity;

import jakarta.persistence.AttributeConverter;
import me.zedaster.authservice.model.Role;

public class RoleConverter implements AttributeConverter<Role, Integer>  {
    @Override
    public Integer convertToDatabaseColumn(Role role) {
        return role.getId();
    }

    @Override
    public Role convertToEntityAttribute(Integer roleId) {
        return Role.getById(roleId);
    }
}
