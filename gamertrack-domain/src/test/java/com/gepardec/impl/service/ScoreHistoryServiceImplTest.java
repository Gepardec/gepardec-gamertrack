package com.gepardec.impl.service;

import com.gepardec.TestFixtures;
import com.gepardec.core.repository.ScoreHistoryRepository;
import com.gepardec.core.services.TokenService;
import com.gepardec.model.ScoreHistory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScoreHistoryServiceImplTest {

    @Mock
    ScoreHistoryRepository scoreHistoryRepository;

    @Mock
    TokenService tokenService;

    @InjectMocks
    ScoreHistoryServiceImpl scoreHistoryService;

    @Test
    void ensureSaveScoreHistoryGeneratesTokenAndReturnsSavedScoreHistory() {
        ScoreHistory scoreHistory = TestFixtures.scoreHistory(1L, 1L, 1L);
        scoreHistory.setToken(null);

        when(tokenService.generateToken()).thenReturn("generatedToken");
        when(scoreHistoryRepository.saveScoreHistory(any())).thenReturn(Optional.of(scoreHistory));

        var savedScoreHistory = scoreHistoryService.saveScoreHistory(scoreHistory);

        assertTrue(savedScoreHistory.isPresent());
        assertEquals("generatedToken", scoreHistory.getToken());
        verify(scoreHistoryRepository).saveScoreHistory(scoreHistory);
    }

    @Test
    void ensureFindScoreHistoryByTokenReturnsScoreHistoryForExistingScoreHistory() {
        ScoreHistory scoreHistory = TestFixtures.scoreHistory(1L, 1L, 1L);

        when(scoreHistoryRepository.findScoreHistoryByToken(anyString())).thenReturn(
                Optional.of(scoreHistory));

        assertEquals(scoreHistory,
                scoreHistoryService.findScoreHistoryByToken(scoreHistory.getToken()).get());
    }

    @Test
    void ensureFindScoreHistoryByTokenReturnsOptionalEmptyForNonExistingScoreHistory() {
        when(scoreHistoryRepository.findScoreHistoryByToken(anyString())).thenReturn(
                Optional.empty());

        assertEquals(Optional.empty(), scoreHistoryService.findScoreHistoryByToken("noToken"));
    }

    @Test
    void ensureFilterScoreHistoryReturnsListOfScoreHistories() {
        List<ScoreHistory> scoreHistories = TestFixtures.scoreHistories(5);

        when(scoreHistoryRepository.filterScoreHistory(any(), any(), any())).thenReturn(
                scoreHistories);

        var foundScoreHistories = scoreHistoryService.filterScoreHistory(null, null, null);

        assertEquals(scoreHistories.size(), foundScoreHistories.size());
        assertEquals(scoreHistories, foundScoreHistories);
    }

    @Test
    void ensureDeleteScoreHistoryByGameDelegatesToRepository() {
        scoreHistoryService.deleteScoreHistoryByGame("gameToken");

        verify(scoreHistoryRepository).deleteScoreHistoryByGame("gameToken");
    }
}
