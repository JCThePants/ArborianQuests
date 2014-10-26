/* This file is part of ArborianQuests for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


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
