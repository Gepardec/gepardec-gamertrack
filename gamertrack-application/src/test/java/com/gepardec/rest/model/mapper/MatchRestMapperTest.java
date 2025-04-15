package com.gepardec.rest.model.mapper;

import static com.gepardec.RestTestFixtures.updateMatchCommand;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gepardec.RestTestFixtures;
import com.gepardec.impl.service.TokenServiceImpl;
import com.gepardec.model.Match;
import com.gepardec.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MatchRestMapperTest {

  @InjectMocks
  private MatchRestMapper matchRestMapper;
  private final TokenServiceImpl tokenService = new TokenServiceImpl();


  @Test
  void ensureCreateMatchToMatchDtoWorks() {
    Match mappedMatch =
        matchRestMapper.createMatchCommandtoMatch(RestTestFixtures.createMatchCommand());

    assertNotNull(mappedMatch);
    assertEquals(mappedMatch.getGame().getId(),
        RestTestFixtures.createMatchCommand().game().getId());
    assertTrue(mappedMatch.getUsers().stream().map(User::getId).toList()
        .containsAll(
            RestTestFixtures.createMatchCommand().users().stream().map(User::getId).toList()));
  }

  @Test
  void ensureUpdateMatchToMatchDtoWorks() {
    Match mappedMatch = matchRestMapper
        .updateMatchCommandtoMatch(updateMatchCommand().game().getId(),
            tokenService.generateToken(), updateMatchCommand());

    assertDoesNotThrow(() -> NullPointerException.class);
    assertNotNull(mappedMatch);
    assertEquals(mappedMatch.getGame().getId(),
        updateMatchCommand().game().getId());
    assertTrue(mappedMatch.getUsers().stream().map(User::getId).toList()
        .containsAll(
            updateMatchCommand().users().stream().map(User::getId).toList()));
  }
}
