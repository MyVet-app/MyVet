package com.myvet.myvet

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UpdatePetDetailsUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(PetOwnerWindow::class.java)

    @Before
    fun setup() {
        Intents.init() // Initialize Espresso Intents
    }

    @After
    fun tearDown() {
        Intents.release() // Release Espresso Intents
    }

    @Test
    fun testUpdatePetDetails() {
        // 1. Navigate to 'Update Pet Details' screen
        onView(withId(R.id.UpdateDetails)) // Locate the 'Update Pet Details' button
            .perform(click())

        // Verify that the 'Update Pet Details' screen is opened
        Intents.intended(hasComponent(UpdatePetDetails::class.java.name))

        // 2. Fill in the new pet details
        onView(withId(R.id.PetName)).perform(typeText("Buddy"), closeSoftKeyboard())
        onView(withId(R.id.typeOfPet)).perform(typeText("Dog"), closeSoftKeyboard())
        onView(withId(R.id.PetAge)).perform(typeText("3"), closeSoftKeyboard())
        onView(withId(R.id.weight)).perform(typeText("15"), closeSoftKeyboard())
        onView(withId(R.id.gender)).perform(typeText("Male"), closeSoftKeyboard())
        onView(withId(R.id.medicalHistory)).perform(typeText("No known issues"), closeSoftKeyboard())

        // 3. Click the 'Update' button
        onView(withId(R.id.update)).perform(click())

        // 4. Verify the success message is displayed
        onView(withId(R.id.errorMessage))
            .check(matches(isDisplayed()))
            .check(matches(withText("הפרטים עודכנו בהצלחה")))

        // 5. Verify redirection to the home screen
        Intents.intended(hasComponent(PetOwnerWindow::class.java.name))
    }
}
