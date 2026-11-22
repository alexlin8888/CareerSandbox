package com.careersandbox.app.ui.screens.interview

// State handed from the history list to the report screen.
internal object ReportViewer {
    // True while the report screen shows a stored report instead of a fresh one.
    var viewingHistory: Boolean = false

    // The user's own utterances, for the group collaboration summary.
    var userSays: List<String> = emptyList()
}