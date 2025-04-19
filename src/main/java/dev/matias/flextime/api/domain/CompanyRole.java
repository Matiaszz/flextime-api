package dev.matias.flextime.api.domain;

import lombok.Getter;

import java.util.List;

@Getter
public enum CompanyRole {
    COMPANY("ROLE_COMPANY");

    private final String role;


    CompanyRole(String role){
        this.role = role;
    }

    public List<String> getPermissions() {
        return List.of("ROLE_COMPANY");
    }



}
