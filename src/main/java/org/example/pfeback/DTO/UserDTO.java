package org.example.pfeback.DTO;


import lombok.Getter;
import lombok.Setter;
import org.example.pfeback.model.Department;
import org.example.pfeback.model.Role;

@Getter
@Setter
public class UserDTO {

    private Integer id;
    private String firstname;
    private String lastname;
    private String username;
    private String password;
    private Role role;
    private Department department;
}
