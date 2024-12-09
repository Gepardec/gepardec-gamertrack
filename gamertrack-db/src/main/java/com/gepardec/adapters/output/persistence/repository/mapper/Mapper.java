package com.gepardec.adapters.output.persistence.repository.mapper;

import com.gepardec.model.Game;
import com.gepardec.model.GameOutcome;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import com.gepardec.model.dto.ScoreDto;
import com.gepardec.model.dto.UserDto;
import com.gepardec.model.dtos.GameDto;
import com.gepardec.model.dtos.GameOutcomeDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class Mapper {

    @PersistenceContext()
    protected EntityManager entityManager;

    public User toUser(UserDto userDTO) {
        return new User(userDTO.firstname(), userDTO.lastname(),userDTO.deactivated());
    }
    public User toExistingUser(UserDto userDTO, User user) {
        user.setFirstname(userDTO.firstname());
        user.setLastname(userDTO.lastname());
        user.setDeactivated(userDTO.deactivated());
        return user;
    }

    public Score toScore(ScoreDto scoreDto) {
        return new Score(entityManager.getReference(User.class, scoreDto.userId()), entityManager.getReference(Game.class, scoreDto.gameId()),scoreDto.scorePoints());
    }
    public Score toExistingScore(ScoreDto scoreDto, Score score) {
        score.setUser(entityManager.getReference(User.class, scoreDto.userId()));
        score.setGame(entityManager.getReference(Game.class, scoreDto.gameId()));
        score.setScorePoints(scoreDto.scorePoints());
        return score;
    }


    public GameOutcome toGameOutcomeWithReference(GameOutcomeDto gameOutcomeDto) {
        if (gameOutcomeDto == null) {
            return null;
        }
        GameOutcome gameOutcome = new GameOutcome();
        gameOutcome.setGame(gameReference(gameOutcomeDto.gameId()));
        gameOutcome.setUsers(gameOutcomeDto.userIds()
            .stream()
            .map(this::userReference).toList());
        return gameOutcome;
    }

    public GameOutcome toGameOutcomeWithReference(GameOutcomeDto gameOutcomeDto,
        GameOutcome gameOutcome) {
        if (gameOutcomeDto == null && gameOutcome == null) {
            return null;
        }

        List<User> users = new ArrayList<>();

        gameOutcomeDto.userIds()
            .forEach(userReference -> users.add(
                entityManager.getReference(User.class, userReference)));

        gameOutcome.setGame(gameReference(gameOutcomeDto.gameId()));
        gameOutcome.setUsers(users);
        return gameOutcome;
    }

    public Game toGame(GameDto gameDto) {
        return toGame(gameDto, new Game());
    }

    public Game toGame(GameDto gameDto, Game game) {
        game.setRules(gameDto.rules());
        game.setName(gameDto.title());

        return game;
    }

    public Game gameReference(Long id) {
        return entityManager.getReference(Game.class, id);
    }

    public User userReference(Long id) {
        return entityManager.getReference(User.class, id);
    }

}
