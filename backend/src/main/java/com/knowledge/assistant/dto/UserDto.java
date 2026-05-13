package com.knowledge.assistant.dto;

import com.knowledge.assistant.entity.Role;

public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;

    public UserDto() {}

    public UserDto(Long id, String firstName, String lastName, String email, Role role) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public static UserDtoBuilder builder() {
        return new UserDtoBuilder();
    }

    public static class UserDtoBuilder {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private Role role;

        public UserDtoBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public UserDtoBuilder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserDtoBuilder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserDtoBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserDtoBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public UserDto build() {
            return new UserDto(id, firstName, lastName, email, role);
        }
    }
}
