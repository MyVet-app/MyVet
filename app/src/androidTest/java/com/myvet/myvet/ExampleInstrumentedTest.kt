//package com.myvet.myvet
//
//import androidx.test.espresso.Espresso.onView
//import androidx.test.espresso.action.ViewActions.click
//import androidx.test.espresso.action.ViewActions.typeText
//import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
//import androidx.test.espresso.assertion.ViewAssertions.matches
//import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
//import androidx.test.espresso.matcher.ViewMatchers.withId
//import androidx.test.espresso.matcher.ViewMatchers.withText
//import androidx.test.ext.junit.rules.ActivityScenarioRule
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@RunWith(AndroidJUnit4::class)
//class MainUITest {
//
//    @get:Rule
//    val activityRule = ActivityScenarioRule(MainActivity::class.java)
//
//    @Test
//    fun testLoginAndNavigateToOwnerSignUp() {
//        // נוודא שהמשתמש התחבר בהצלחה (Firebase AuthUI מחזיר את המסך הראשי)
//        onView(withId(R.id.main)) // מזהה את הלייאאוט הראשי שמוצג אחרי ההתחברות
//            .check(matches(isDisplayed()))
//
////        // נוודא שאם המשתמש חדש, אנחנו מנווטים למסך ההרשמה
////        onView(withId(R.id.pet_button)) // כפתור "בעל חיית מחמד"
////            .check(matches(isDisplayed()))
////
////        // נלחץ על כפתור "בעל חיית מחמד" כדי לעבור למסך OwnerSignUp
////        onView(withId(R.id.pet_button))
////            .perform(click())
////
////        // נוודא שהגענו למסך ההרשמה של בעל חיית מחמד
////        onView(withId(R.id.homeAddress))
////            .check(matches(isDisplayed()))
//    }
//
//    @Test
//    fun testUpdatePetDetails() {
//        // נוודא שהגענו למסך PetOwnerWindow
//        onView(withId(R.id.UpdateDetails))
//            .check(matches(isDisplayed()))
//
//        // נלחץ על כפתור עדכון פרטים
//        onView(withId(R.id.UpdateDetails))
//            .perform(click())
//
//        // נוודא שהגענו למסך UpdatePetDetails
//        onView(withId(R.id.PetName))
//            .check(matches(isDisplayed()))
//
//        // נמלא את פרטי החיה
//        onView(withId(R.id.PetName))
//            .perform(typeText("בובי"), closeSoftKeyboard())
//        onView(withId(R.id.typeOfPet))
//            .perform(typeText("כלב"), closeSoftKeyboard())
//        onView(withId(R.id.PetAge))
//            .perform(typeText("5"), closeSoftKeyboard())
//        onView(withId(R.id.weight))
//            .perform(typeText("10"), closeSoftKeyboard())
//        onView(withId(R.id.gender))
//            .perform(typeText("זכר"), closeSoftKeyboard())
//        onView(withId(R.id.medicalHistory))
//            .perform(typeText("בריא לגמרי"), closeSoftKeyboard())
//
//        // נלחץ על כפתור "עדכן"
//        onView(withId(R.id.update))
//            .perform(click())
//
//        // נוודא שההודעה על הצלחת העדכון מוצגת
//        onView(withId(R.id.errorMessage))
//            .check(matches(isDisplayed()))
//            .check(matches(withText("הפרטים עודכנו בהצלחה")))
//    }
//}
