package com.gepardec.rest.model.mapper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.gepardec.RestTestFixtures;
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


  @Test
  void ensureCreateMatchCmdToMatchDtoWorks() {
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
  void ensureUpdateMatchCmdToMatchDtoWorks() {
    Match mappedMatch = matchRestMapper
        .updateMatchCommandtoMatch(RestTestFixtures.updateMatchCommand().game().getId(),
            RestTestFixtures.updateMatchCommand());

    assertDoesNotThrow(() -> NullPointerException.class);
    assertNotNull(mappedMatch);
    assertEquals(mappedMatch.getGame().getId(),
        RestTestFixtures.updateMatchCommand().game().getId());
    assertTrue(mappedMatch.getUsers().stream().map(User::getId).toList()
        .containsAll(
            RestTestFixtures.updateMatchCommand().users().stream().map(User::getId).toList()));
  }
}
