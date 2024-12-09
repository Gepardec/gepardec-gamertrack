package com.gepardec.adapters.output.persistence.repository.mapper;

import com.gepardec.TestFixtures;
import com.gepardec.model.Game;
import com.gepardec.model.Score;
import com.gepardec.model.User;
import com.gepardec.model.dto.ScoreDto;
import com.gepardec.model.dto.UserDto;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MapperTest {

    @InjectMocks
    Mapper mapper = new Mapper();
    @Mock
    EntityManager entityManager;

    @Test
    public void ensureMapUserDtoToUserWorks() {
        User user = TestFixtures.user(1L);

        UserDto userDto = new UserDto(user);

        User mappedUser = mapper.toUser(userDto);

        assertEquals(mappedUser.getFirstname(), user.getFirstname());
        assertEquals(mappedUser.getLastname(), user.getLastname());
        assertEquals(mappedUser.isDeactivated(), user.isDeactivated());
    }

    @Test
    public void ensureMapUserDtoToExistingUserWorks() {
        User user = TestFixtures.user(1L);

        UserDto userDto = new UserDto(user);

        User mappedUser = mapper.toExistingUser(userDto,user);

        assertEquals(mappedUser.getId(), user.getId());
        assertEquals(mappedUser.getFirstname(), user.getFirstname());
        assertEquals(mappedUser.getLastname(), user.getLastname());
        assertEquals(mappedUser.isDeactivated(), user.isDeactivated());
    }

    @Test
    public void ensureMapScoreDtoToScoreWorks() {
        Score score = TestFixtures.score(1L,1L,1L);

        ScoreDto scoreDto = new ScoreDto(score);

        when(entityManager.getReference(User.class, scoreDto.userId())).thenReturn(TestFixtures.user(1L));
        when(entityManager.getReference(Game.class, scoreDto.gameId())).thenReturn(TestFixtures.game(1L));

        Score mappedScore = mapper.toScore(scoreDto);

        assertEquals(mappedScore.getUser().getId(), scoreDto.userId());
        assertEquals(mappedScore.getGame().getId(), scoreDto.gameId());
        assertEquals(mappedScore.getScorePoints(), scoreDto.scorePoints());
    }

    @Test
    public void ensureMapScoreDtoToExistingScoreWorks() {
        Score score = TestFixtures.score(1L,1L,1L);

        ScoreDto scoreDto = new ScoreDto(score);

        when(entityManager.getReference(User.class, scoreDto.userId())).thenReturn(TestFixtures.user(1L));
        when(entityManager.getReference(Game.class, scoreDto.gameId())).thenReturn(TestFixtures.game(1L));

        Score mappedScore = mapper.toExistingScore(scoreDto,score);

        assertEquals(mappedScore.getId(), scoreDto.id());
        assertEquals(mappedScore.getUser().getId(), scoreDto.userId());
        assertEquals(mappedScore.getGame().getId(), scoreDto.gameId());
        assertEquals(mappedScore.getScorePoints(), scoreDto.scorePoints());
    }
}
