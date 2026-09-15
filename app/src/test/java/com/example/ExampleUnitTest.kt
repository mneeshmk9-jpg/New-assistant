package com.example

import com.example.certification.MissionCertificationEngine
import com.example.certification.PlannedAction
import com.example.database.GoldenMission
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testMultiStepRoutineCertification() {
        val routineMission = GoldenMission(
            id = "GM-ROUTINE-01",
            category = "Smart Home",
            title = "Multi-Device Night Routine",
            description = "Verify compound routine executing multi-step smart home actions and morning reminder scheduling",
            sampleQueries = "start night routine, turn off all lights and set morning alarm",
            expectedActionsJson = "[{\"action\":\"control_device\",\"tool\":\"control_device\"},{\"action\":\"create_reminder\",\"tool\":\"create_reminder\"}]",
            requiredAnchorsJson = "[\"DEVICE_STATE_CHANGED\", \"REMINDER_PERSISTED\", \"AUDIT_LOG_RECORDED\"]",
            minPlanningScore = 85.0,
            minExecutionScore = 85.0
        )

        val plannedActions = listOf(
            PlannedAction(action = "control_device", parameters = mapOf(Pair("tool", "control_device"), Pair("device", "All Lights"), Pair("state", "OFF"))),
            PlannedAction(action = "create_reminder", parameters = mapOf(Pair("tool", "create_reminder"), Pair("task", "Morning Alarm 7AM")))
        )

        val runtimeTraces = listOf("DEVICE_STATE_CHANGED", "REMINDER_PERSISTED", "AUDIT_LOG_RECORDED")

        val cert = MissionCertificationEngine.certifyMission(
            userQuery = "start night routine",
            plannedActions = plannedActions,
            runtimeTraces = runtimeTraces,
            goldenMission = routineMission
        )

        assertEquals("CERTIFIED", cert.status)
        assertEquals(100.0, cert.planningScore, 0.1)
        assertEquals(100.0, cert.executionScore, 0.1)
    }

    @Test
    fun testSecurityPerimeterLockdownCertification() {
        val securityMission = GoldenMission(
            id = "GM-SECURITY-01",
            category = "Security",
            title = "Home Perimeter Security Lockdown",
            description = "Verify high-security perimeter locking triggers device state changes and mandatory audit logging",
            sampleQueries = "lock down all front doors and activate security cameras, arm perimeter alarm",
            expectedActionsJson = "[{\"action\":\"control_device\",\"tool\":\"control_device\"}]",
            requiredAnchorsJson = "[\"DEVICE_STATE_CHANGED\", \"POLICY_CHECKED\", \"USER_CONFIRMED\", \"AUDIT_LOG_RECORDED\"]",
            minPlanningScore = 90.0,
            minExecutionScore = 90.0
        )

        val plannedActions = listOf(
            PlannedAction(action = "control_device", parameters = mapOf(Pair("tool", "control_device"), Pair("device", "Front Lock"), Pair("state", "LOCKED")))
        )

        // Fully confirmed and policy checked runtime traces
        val validTraces = listOf("DEVICE_STATE_CHANGED", "POLICY_CHECKED", "USER_CONFIRMED", "AUDIT_LOG_RECORDED")

        val certifiedResult = MissionCertificationEngine.certifyMission(
            userQuery = "lock down front doors",
            plannedActions = plannedActions,
            runtimeTraces = validTraces,
            goldenMission = securityMission
        )

        assertEquals("CERTIFIED", certifiedResult.status)
        assertTrue(certifiedResult.totalScore >= 90.0)

        // Missing USER_CONFIRMED trace should fail security policy
        val unconfirmedTraces = listOf("DEVICE_STATE_CHANGED", "POLICY_CHECKED", "AUDIT_LOG_RECORDED")

        val failedResult = MissionCertificationEngine.certifyMission(
            userQuery = "lock down front doors",
            plannedActions = plannedActions,
            runtimeTraces = unconfirmedTraces,
            goldenMission = securityMission
        )

        assertEquals("FAILED", failedResult.status)
        assertTrue(failedResult.certificationNotes.contains("USER_CONFIRMED"))
    }

    @Test
    fun testMessagingMissingParameterPenalty() {
        val messagingMission = GoldenMission(
            id = "GM-MESSAGING-02",
            category = "Messaging",
            title = "Urgent Emergency Contact Alert",
            description = "Verify high-priority SMS alert requires policy validation, valid recipient/message params, and explicit confirmation",
            sampleQueries = "alert emergency contacts that I am in trouble, send urgent sms to doctor",
            expectedActionsJson = "[{\"action\":\"send_sms\",\"tool\":\"send_sms\"}]",
            requiredAnchorsJson = "[\"SMS_PREPARED\", \"POLICY_CHECKED\", \"USER_CONFIRMED\", \"AUDIT_LOG_RECORDED\"]",
            minPlanningScore = 90.0,
            minExecutionScore = 90.0
        )

        // Missing required 'recipient' parameter
        val incompleteActions = listOf(
            PlannedAction(action = "send_sms", parameters = mapOf(Pair("tool", "send_sms"), Pair("message", "I am in trouble")))
        )

        val traces = listOf("SMS_PREPARED", "POLICY_CHECKED", "USER_CONFIRMED", "AUDIT_LOG_RECORDED")

        val cert = MissionCertificationEngine.certifyMission(
            userQuery = "send urgent sms to doctor",
            plannedActions = incompleteActions,
            runtimeTraces = traces,
            goldenMission = messagingMission
        )

        assertEquals("FAILED", cert.status)
        assertTrue(cert.certificationNotes.contains("PARAMETER ERROR"))
    }
}

