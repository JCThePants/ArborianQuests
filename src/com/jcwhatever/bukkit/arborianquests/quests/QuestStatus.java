package com.jcwhatever.bukkit.arborianquests.quests;


public enum QuestStatus {
    /**
     * The player has not accepted nor completed the quest.
     */
    NONE        (CurrentQuestStatus.NONE,        QuestCompletionStatus.NOT_COMPLETED),
    /**
     * The player has accepted the quest but has not completed it.
     */
    INCOMPLETE  (CurrentQuestStatus.IN_PROGRESS, QuestCompletionStatus.NOT_COMPLETED),
    /**
     * The player has completed the quest.
     */
    COMPLETED   (CurrentQuestStatus.NONE,        QuestCompletionStatus.COMPLETED),
    /**
     * The player has completed the quest and is currently
     * re-running the quest. Same as incomplete but means the
     * player has completed the quest in the past.
     */
    RERUN       (CurrentQuestStatus.IN_PROGRESS, QuestCompletionStatus.COMPLETED);


    public enum CurrentQuestStatus {
        /**
         * The player is not in the quest.
         */
        NONE,
        /**
         * The player is in the quest.
         */
        IN_PROGRESS
    }

    public enum QuestCompletionStatus {
        /**
         * The player has completed the quest at least 1 time.
         */
        COMPLETED,
        /**
         * The player has never completed the quest.
         */
        NOT_COMPLETED
    }

    private final CurrentQuestStatus _currentStatus;
    private final QuestCompletionStatus _completionStatus;

    QuestStatus(CurrentQuestStatus currentStatus, QuestCompletionStatus completionStatus) {
        _currentStatus = currentStatus;
        _completionStatus = completionStatus;
    }

    public CurrentQuestStatus getCurrentStatus() {
        return _currentStatus;
    }

    public QuestCompletionStatus getCompletionStatus() {
        return _completionStatus;
    }
}
