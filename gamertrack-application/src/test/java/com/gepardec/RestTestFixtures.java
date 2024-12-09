package com.gepardec;

import com.gepardec.model.Game;
import com.gepardec.rest.model.command.CreateScoreCommand;
import com.gepardec.rest.model.command.CreateUserCommand;
import com.gepardec.rest.model.command.UpdateUserCommand;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class RestTestFixtures {
    public static CreateScoreCommand createScoreCommand(Long id) {
        CreateScoreCommand createScoreCommand = new CreateScoreCommand(1L,1L,10);
        return createScoreCommand;
    }
    public static CreateUserCommand createUserCommand(Long id) {
        CreateUserCommand createUserCommand = new CreateUserCommand("Max","Muster");
        return createUserCommand;
    }
    public static UpdateUserCommand updateUserCommand(Long id) {
        UpdateUserCommand updateUserCommand = new UpdateUserCommand("Max","Muster",false);
        return updateUserCommand;
    }
}
