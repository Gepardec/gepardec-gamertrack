package com.gepardec.adapters.output.persistence.repository.mapper;

import com.gepardec.model.Game;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import com.gepardec.model.dto.ScoreDto;
import com.gepardec.model.dto.UserDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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
}
