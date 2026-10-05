package com.gepardec.rest.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.gepardec.model.Tournament;
import com.gepardec.model.User;
import com.gepardec.rest.model.command.CreateTournamentCommand;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TournamentRestMapperTest {

  @InjectMocks
  private TournamentRestMapper tournamentRestMapper;


  @Test
  void ensureCreateTournamentCommandToTournamentWorks() {
    CreateTournamentCommand command = new CreateTournamentCommand("Office Darts Cup",
        "gameToken123", List.of("userToken1", "userToken2"));

    Tournament mappedTournament =
        tournamentRestMapper.createTournamentCommandToTournament(command);

    assertNotNull(mappedTournament);
    assertEquals(command.name(), mappedTournament.getName());
    assertEquals(command.gameToken(), mappedTournament.getGame().getToken());
    assertEquals(command.participantTokens(),
        mappedTournament.getParticipants().stream().map(User::getToken).toList());
    assertNull(mappedTournament.getState());
    assertNull(mappedTournament.getToken());
  }

  @Test
  void ensureUsersFromTokensMapsTokensInOrder() {
    List<User> mappedUsers =
        tournamentRestMapper.usersFromTokens(List.of("winnerToken", "loserToken"));

    assertEquals(List.of("winnerToken", "loserToken"),
        mappedUsers.stream().map(User::getToken).toList());
  }

  @Test
  void ensureUsersFromTokensForNullReturnsNull() {
    assertNull(tournamentRestMapper.usersFromTokens(null));
  }
}
