package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.assistant.ActionType
import com.example.assistant.DharamAssistantEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Dharam", appName)
    }

    @Test
    fun `dharam engine greeting test`() {
        val engine = DharamAssistantEngine()
        val (greeting, followUp) = engine.getGreeting()
        assertTrue(greeting.contains("Namaste") || greeting.contains("Assistant"))
        assertTrue(followUp.contains("aaj kya kaam karna hai"))
    }

    @Test
    fun `dharam engine queries test`() {
        val engine = DharamAssistantEngine()

        // Time command
        val timeResult = engine.processQuery("kitna time hua hai")
        assertEquals(ActionType.TIME, timeResult.actionType)
        assertTrue(timeResult.speechText.contains("Abhi time ho raha hai"))

        // YouTube command
        val ytResult = engine.processQuery("open youtube")
        assertEquals(ActionType.YOUTUBE, ytResult.actionType)
        assertTrue(ytResult.speechText.contains("YouTube"))

        // Beat command
        val beatResult = engine.processQuery("beat bajao")
        assertEquals(ActionType.BEAT_STUDIO, beatResult.actionType)
        assertTrue(beatResult.speechText.contains("Sahi hai lala"))

        // Stop command
        val stopResult = engine.processQuery("bye dharam alvida")
        assertEquals(ActionType.STOP, stopResult.actionType)
        assertTrue(stopResult.speechText.contains("Thik hai, phir milte hain"))
    }
}
