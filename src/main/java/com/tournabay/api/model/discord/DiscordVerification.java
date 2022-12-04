package com.tournabay.api.model.discord;

import com.tournabay.api.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DiscordVerification {

    public DiscordVerification(User user) {
        this.state = UUID.randomUUID().toString();
        this.userId = user.getId();
    }

    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    private Long id;

    private String state;
    private Long userId;
}
