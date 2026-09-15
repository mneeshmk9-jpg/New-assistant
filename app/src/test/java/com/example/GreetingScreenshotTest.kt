package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AgentStep
import com.example.viewmodel.StepStatus
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val mockSteps = listOf(
      AgentStep(action = "open_app", description = "Opening Google Maps", status = StepStatus.COMPLETED),
      AgentStep(action = "send_sms", description = "Sending SMS to Rahul: I will be 10 mins late", status = StepStatus.RUNNING),
      AgentStep(action = "create_reminder", description = "Create Reminder: Call mom in 30 mins", status = StepStatus.PENDING)
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        AgentWorkflowPanel(steps = mockSteps)
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}
