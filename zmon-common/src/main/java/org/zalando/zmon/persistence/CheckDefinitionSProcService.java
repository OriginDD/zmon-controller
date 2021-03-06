package org.zalando.zmon.persistence;

import java.util.Date;
import java.util.List;

import org.zalando.zmon.domain.CheckDefinition;
import org.zalando.zmon.domain.CheckDefinitionImport;
import org.zalando.zmon.domain.CheckDefinitions;
import org.zalando.zmon.domain.DefinitionStatus;
import org.zalando.zmon.domain.HistoryEntry;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcParam;
import de.zalando.sprocwrapper.SProcService;

@SProcService
public interface CheckDefinitionSProcService {

    @SProcCall
    List<CheckDefinition> getCheckDefinitions(@SProcParam DefinitionStatus status,
            @SProcParam List<Integer> checkDefinitionIds);

    @SProcCall
    List<CheckDefinition> getCheckDefinitionsByOwningTeam(@SProcParam DefinitionStatus status,
            @SProcParam List<String> owningTeams);

    @SProcCall
    CheckDefinitions getAllCheckDefinitions(@SProcParam DefinitionStatus status);

    @SProcCall
    CheckDefinitions getCheckDefinitionsDiff(@SProcParam Long lastSnapshotId);

    @SProcCall
    CheckDefinitionImportResult createOrUpdateCheckDefinition(@SProcParam CheckDefinitionImport checkDefinition);

    @SProcCall
    CheckDefinition deleteCheckDefinition(@SProcParam String userName, @SProcParam String name,
            @SProcParam String owningTeam);

    @SProcCall
    List<CheckDefinition> deleteDetachedCheckDefinitions();

    @SProcCall
    List<HistoryEntry> getCheckDefinitionHistory(@SProcParam int checkDefinitionId, @SProcParam int limit,
            @SProcParam Date from, @SProcParam Date to);

    @SProcCall
    List<Integer> deleteUnusedCheckDefinition(@SProcParam int id, @SProcParam List<String> teams);

    @SProcCall
    Date getCheckLastModifiedMax();
}
