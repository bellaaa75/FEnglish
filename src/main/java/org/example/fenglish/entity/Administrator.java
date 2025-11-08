package org.example.fenglish.entity;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "administrator")
@PrimaryKeyJoinColumn(name = "userId") // 与父表User的userID关联
public class Administrator extends User {
    public Administrator(String userId, String userPassword) {
        super(userId, userPassword, "ADMIN");
    }

    public Administrator() {
    }


}