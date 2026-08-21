package com.gepardec.core.services;

import com.gepardec.model.HeadToHead;
import com.gepardec.model.PlayerForm;
import com.gepardec.model.PlayerGameStats;

import java.util.Optional;

public interface StatisticsService {

    Optional<PlayerGameStats> getPlayerGameStats(String userToken, String gameToken);

    Optional<PlayerForm> getPlayerForm(String userToken, String gameToken, int maxResults);

    Optional<HeadToHead> getHeadToHead(String firstUserToken, String secondUserToken, String gameToken);
}
