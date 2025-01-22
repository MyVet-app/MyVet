package com.myvet.myvet

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

@RunWith(AndroidJUnit4::class)
class MyUITests {

    @Test
    fun testPetButtonNavigation() {
        // לחץ על כפתור ה-Pet
        onView(withId(R.id.pet_button))
            .perform(click())

        // ודא שהמסך הבא (OwnerSignUp) מוצג על ידי חיפוש שדה כתובת
        onView(withId(R.id.homeAddress))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testVetButtonNavigation() {
        // לחץ על כפתור ה-Vet
        onView(withId(R.id.vet_button))
            .perform(click())

        // ודא שהמסך הבא (VeterinarianSignUp) מוצג
        onView(withId(R.id.homeAddress)) // כאן צריך לעדכן לפי המסך הנכון של ה-Vet
            .check(matches(isDisplayed()))
    }

    @Test
    fun testRegisterButtonEnabledWhenAddressIsEntered() {
        // הזן כתובת
        onView(withId(R.id.homeAddress))
            .perform(typeText("כתובת לדוגמה"))

        // ודא שכפתור ההרשמה הופך לזמין
        onView(withId(R.id.submitButton))
            .check(matches(isEnabled()))
    }

    @Test
    fun testErrorMessageVisibilityWhenAddressIsEmpty() {
        // ודא שההודעה לא מוצגת בהתחלה
        onView(withId(R.id.errorMessage))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))

        // השאר את שדה הכתובת ריק
        onView(withId(R.id.homeAddress))
            .perform(typeText(""))

        // ודא שההודעה מוצגת כשהכתובת ריקה
        onView(withId(R.id.errorMessage))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }
}
