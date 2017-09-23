//package com.wdziemia.githubtimes.scratch;
//
//
//public class Action {
//
//    private long insertIssue(Issue issue) {
//        if (recordExists(Issue.TABLE_NAME, Issue.ID, String.valueOf(issue.id()))) {
//            return 0;
//        }
//
//        return db.insert(Issue.TABLE_NAME, new Issue.Marshal()
//                .url(issue.url())
//                .id(issue.id()));
//    }
//
//}
